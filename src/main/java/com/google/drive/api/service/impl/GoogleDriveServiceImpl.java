package com.google.drive.api.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.tika.Tika;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.DriveApiConstants.GOOGLEAPI;
import com.google.drive.api.DriveApiConstants.MSGERROR;
import com.google.drive.api.domain.DriveFile;
import com.google.drive.api.domain.DriveFileList;
import com.google.drive.api.exception.GoogleApiException;
import com.google.drive.api.exception.GoogleApiGeneralErrorException;
import com.google.drive.api.exception.GoogleApiSecurityException;
import com.google.drive.api.service.GoogleDriveService;

/**
 * Implementacion of interface {@link GoogleDriveService} with basic methods related to Google Drive files
 * and their metadata.
 * 
 * @author leonardo.ramos
 *
 */
public class GoogleDriveServiceImpl implements GoogleDriveService {
	
	private GoogleCredentials credentials;
	private Drive driveService;
	
	/**
	 * Default constructor.
	 * 
	 * @throws GeneralSecurityException
	 */
	public GoogleDriveServiceImpl() throws GeneralSecurityException {
		this.initializeService();
	}
	
	/**
	 * Constructor of a new instance of {@link GoogleDriveServiceImpl}. In case of a valid non null instance of {@link Drive} 
	 * and {@link GoogleCredentials}, it's expected that the credentials are already initialized on  the outside context in 
	 * which this class was constructed and used.
	 * 
	 * @param driveService
	 * @param credentials
	 * @throws GoogleApiSecurityException
	 */
	protected GoogleDriveServiceImpl(Drive driveService, GoogleCredentials credentials) throws GoogleApiSecurityException {
		if (driveService != null && credentials != null) {
			this.driveService = driveService;
			this.setCredentials(credentials);

		} else {
			this.initializeService();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCredentials(GoogleCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GoogleCredentials getCredentials() {
		return this.credentials;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeService() throws GoogleApiSecurityException {
		try {
			if (this.driveService == null) {

				this.initializeCredentials();
				
				this.driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), 
						GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(this.getCredentials()))
				        .setApplicationName(this.getApplicationName())
				        .build();
			}
			
		} catch (GeneralSecurityException | IOException e) {
			throw new GoogleApiSecurityException(MSGERROR.GOOGLE_OAUTH2_ERROR, e);
		} 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doDeleteFile(String fileId) throws GoogleApiException {
		try {
			this.driveService.files().delete(fileId).execute();

		} catch (Exception e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<DriveFile> doGetFileByNameAndParentFolder(LinkedList<String> folderHierarchy, String fileName) throws GoogleApiGeneralErrorException {
		try {
			String folderId = this.getFolderIdByName(folderHierarchy);
			
			FileList result = this.driveService.files().list()
					.setQ(String.format(GOOGLEAPI.FILE_QUERY_IN_FOLDER, fileName, folderId))
					.setSpaces(GOOGLEAPI.DRIVE_SPACES)
					.setFields(GOOGLEAPI.FILE_FIELDS)
					.execute();
			
			if (this.isEmptyResult(result)) {
				return Optional.empty();
			}
			
			File file = result.getFiles().getFirst();
			
			return Optional.of(this.buildDriveFile(file));
		
		} catch (IOException e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] doDownloadFile(String fileId) throws GoogleApiException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			this.driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
			
			return outputStream.toByteArray();
			
		} catch (Exception e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		} 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DriveFile doUploadFileToFolder(LinkedList<String> folderHierarchy, java.io.File file, boolean isPublic) throws GoogleApiGeneralErrorException {
		try {
			String folderId = this.getFolderIdByName(folderHierarchy);
			
			File fileMetadata = new File();
			fileMetadata.setName(file.getName());
			fileMetadata.setParents(Collections.singletonList(folderId));

			String fileType = new Tika().detect(file);
			FileContent mediaContent = new FileContent(fileType, file);
			
			File uploadedFile = this.driveService.files().create(fileMetadata, mediaContent)
					.setFields(GOOGLEAPI.FILE_FIELDS)
					.execute();
			
			if (uploadedFile == null) {
				throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_UPLOAD_ERROR);
			}
				
			if (isPublic) {
				this.doSetPublicPermission(uploadedFile);
			}
			
			return this.buildDriveFile(uploadedFile);

		} catch (Exception e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSetPublicPermission(File uploadedFile) throws GoogleApiGeneralErrorException {
		try {
			Permission userPermission = new Permission()
			    .setType(GOOGLEAPI.ANYONE_PERMISSION_TYPE)
			    .setRole(GOOGLEAPI.READER_PERMISSION_TOLE);
		
			this.driveService.permissions().create(uploadedFile.getId(), userPermission)
			    .setFields(GOOGLEAPI.ID_FIELD_BASE).execute();

		} catch (IOException e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DriveFileList doGetFolderFiles(LinkedList<String> folderHierarchy, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException {
		List<DriveFile> driveFiles = new ArrayList<>();
		
		try {
			String folderId = this.getFolderIdByName(folderHierarchy);

			FileList result = this.driveService.files().list()
				      .setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER, folderId))
				      .setSpaces(GOOGLEAPI.DRIVE_SPACES)
				      .setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)
				      .setPageSize(pageSize)
				      .setPageToken(pageToken)
				      .execute();
			
			if (!this.isEmptyResult(result)) {
				
				pageToken = result.getNextPageToken();
				
				result.getFiles().forEach(file -> {
					driveFiles.add(this.buildDriveFile(file));
				});
			}
			
		} catch (Exception e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}

		return DriveFileList.builder()
				.driveFiles(driveFiles)
				.pageToken(pageToken)
				.build();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DriveFileList doGetFolderFilesByFileNameFilter(LinkedList<String> folderHierarchy, String fileNameFilter, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException {
		List<DriveFile> driveFiles = new ArrayList<>();
		
		try {
			String folderId = this.getFolderIdByName(folderHierarchy);

			FileList result = this.driveService.files().list()
				      .setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER_FILENAME_FILTER, folderId, fileNameFilter))
				      .setSpaces(GOOGLEAPI.DRIVE_SPACES)
				      .setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)
				      .setPageSize(pageSize)
				      .setPageToken(pageToken)
				      .execute();
			
			if (!this.isEmptyResult(result)) {
				
				pageToken = result.getNextPageToken();
				
				result.getFiles().forEach(file -> {
					driveFiles.add(this.buildDriveFile(file));
				});
			}
			
		} catch (Exception e) {
			throw new GoogleApiGeneralErrorException(MSGERROR.DRIVE_GENERAL_ERROR, e);
		}

		return DriveFileList.builder()
				.driveFiles(driveFiles)
				.pageToken(pageToken)
				.build();
	}
	
	/**
	 * Build an instance of {@link DriveFile} for given Google Drive file data.
	 * 
	 * @param file
	 * @return {@link DriveFile}
	 */
	private DriveFile buildDriveFile(File file) {
		Calendar uploadDate = Calendar.getInstance();
		uploadDate.setTimeInMillis(file.getCreatedTime().getValue());
		
		return DriveFile.builder()
				.fileId(file.getId())
				.fileName(file.getName())
				.uploadDate(uploadDate)
				.build();
	}
	
	/**
	 * Get a folder id for the last folder in a folder tree. If given folder hierarchy do not exist 
	 * (any of the folders) they'll be created.
	 * 
	 * @param folderHierarchy
	 * @return folder id
	 * @throws IOException
	 */
	private String getFolderIdByName(LinkedList<String> folderHierarchy) throws IOException {
		String parentFolderId = null;
		
		for (String folderName : folderHierarchy) {
			
			StringBuilder query = new StringBuilder(String.format(GOOGLEAPI.FOLDER_QUERY_NAME, folderName));
			
			if (parentFolderId != null) {
				query.append(String.format(GOOGLEAPI.FOLDER_QUERY_PARENT, parentFolderId));
			}
			
			FileList result = this.driveService.files().list()
						.setQ(query.toString())
						.setSpaces(GOOGLEAPI.DRIVE_SPACES)
						.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)
						.setPageToken(null)
						.execute();
			
			if (this.isEmptyResult(result)) {
				parentFolderId = this.createFolder(folderName, parentFolderId);
			
			} else {
				File folder = result.getFiles().getFirst();
				parentFolderId = folder.getId();
			}
		}
		
		return parentFolderId;
	}

	/**
	 * Create a new folder in a specific directory (parentFolderId) with a given name and return its folder id.
	 * 
	 * @param folderName
	 * @param parentFolderId
	 * @return folder id
	 * @throws IOException
	 */
	protected String createFolder(String folderName, String parentFolderId) throws IOException {
		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType(GOOGLEAPI.FOLDER_MIME_TYPE);
		
		if (parentFolderId != null && !"".equals(parentFolderId)) {
			fileMetadata.setParents(Collections.singletonList(parentFolderId));
		}

		File folder = this.driveService.files().create(fileMetadata)
			    .setFields(GOOGLEAPI.ID_FIELD_BASE)
			    .execute();
		
		return folder != null ? folder.getId() : null;
	}
	
	/**
	 * Verify if a query result is empty.
	 * 
	 * @param result
	 * @return true if query result is empty false otherwise
	 */
	private boolean isEmptyResult(FileList result) {
		return result == null || result.getFiles() == null || result.getFiles().isEmpty();
	}

}