package com.google.drive.api.service;

import java.util.LinkedList;
import java.util.Optional;

import com.google.api.services.drive.model.File;
import com.google.drive.api.DriveApiConstants.GOOGLEAPI;
import com.google.drive.api.domain.DriveFile;
import com.google.drive.api.domain.DriveFileList;
import com.google.drive.api.exception.GoogleApiException;
import com.google.drive.api.exception.GoogleApiGeneralErrorException;

/**
 * Interface with basic methods and operations regarding Google Drive files.
 * 
 * @author leonardo.ramos
 *
 */
public interface GoogleDriveService extends GoogleService {

	/**
	 * Refresh credentials before deleting a file with given fileId.
	 * 
	 * @param fileId
	 * @throws GoogleApiException
	 */
	default void deleteFile(String fileId) throws GoogleApiException {
		this.refreshCredentials();
		this.doDeleteFile(fileId);
	}
	
	/**
	 * Delete a file with given fileId.
	 * 
	 * @param fileId
	 * @throws GoogleApiException
	 */
	void doDeleteFile(String fileId) throws GoogleApiException;

	/**
	 * Refresh credentials before querying for a file using its folder tree and its file name.
	 * 
	 * @param folderHierarchy
	 * @param fileName
	 * @return {@link Optional<DriveFile>} file retrieved
	 * @throws GoogleApiGeneralErrorException
	 */
	default Optional<DriveFile> getFileByNameAndParentFolder(LinkedList<String> folderHierarchy, String fileName) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		return this.doGetFileByNameAndParentFolder(folderHierarchy, fileName);
	}
	
	/**
	 * Query for a file using its folder tree and its file name.
	 * 
	 * @param folderHierarchy
	 * @param fileName
	 * @return {@link Optional<DriveFile>} file retrieved
	 * @throws GoogleApiGeneralErrorException
	 */
	Optional<DriveFile> doGetFileByNameAndParentFolder(LinkedList<String> folderHierarchy, String fileName) throws GoogleApiGeneralErrorException;

	/**
	 * Refresh credentials before downloading file bytes for given fileId.
	 * 
	 * @param fileId
	 * @return file bytes
	 * @throws GoogleApiException
	 */
	default byte[] downloadFile(String fileId) throws GoogleApiException {
		this.refreshCredentials();
		return this.doDownloadFile(fileId);
	}
	
	/**
	 * Download file bytes for given fileId.
	 * 
	 * @param fileId
	 * @return file bytes
	 * @throws GoogleApiException
	 */
	byte[] doDownloadFile(String fileId) throws GoogleApiException;

	/**
	 * Refresh credentials before uploading a file for a specific folder tree as not public.
	 * 
	 * @param folderHierarchy
	 * @param file
	 * @return {@link DriveFile}
	 * @throws GoogleApiException
	 */
	default DriveFile uploadFileToFolder(LinkedList<String> folderHierarchy, java.io.File file) throws GoogleApiException {
		this.refreshCredentials();
		return this.doUploadFileToFolder(folderHierarchy, file, false);
	}
	
	
	/**
	 * Refresh credentials before uploading a file for a specific folder tree.
	 * 
	 * @param folderHierarchy
	 * @param file
	 * @param isPublic
	 * @return {@link DriveFile}
	 * @throws GoogleApiException
	 */
	default DriveFile uploadFileToFolder(LinkedList<String> folderHierarchy, java.io.File file, boolean isPublic) throws GoogleApiException {
		this.refreshCredentials();
		return this.doUploadFileToFolder(folderHierarchy, file, isPublic);
	}
	
	/**
	 * Upload a file for a specific folder tree.
	 * 
	 * @param folderHierarchy
	 * @param file
	 * @param isPublic
	 * @return {@link DriveFile}
	 * @throws GoogleApiGeneralErrorException
	 */
	DriveFile doUploadFileToFolder(LinkedList<String> folderHierarchy, java.io.File file, boolean isPublic) throws GoogleApiGeneralErrorException;
	
	/**
	 * Refresh credentials before updating permission for a Google Drive file to public.
	 * 
	 * @param file
	 * @throws GoogleApiGeneralErrorException
	 */
	default void setPublicPermission(File file) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		this.doSetPublicPermission(file);
	}
	
	/**
	 * Update permission for a Google Drive file to public.
	 * 
	 * @param file
	 * @throws GoogleApiGeneralErrorException
	 */
	void doSetPublicPermission(File file) throws GoogleApiGeneralErrorException;
	
	/**
	 * Refresh credentials before listing files of a given folder tree.
	 * 
	 * @param folderStructure
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	default DriveFileList getFolderFiles(LinkedList<String> folderStructure) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		return this.doGetFolderFiles(folderStructure, GOOGLEAPI.DEFAULT_PAGE_SIZE, null);
	}
	
	
	/**
	 * Refresh credentials before listing files of a given folder tree.
	 * 
	 * @param folderStructure
	 * @param pageSize
	 * @param pageToken
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	default DriveFileList getFolderFiles(LinkedList<String> folderStructure, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		return this.doGetFolderFiles(folderStructure, pageSize <= GOOGLEAPI.MAX_PAGE_SIZE ? pageSize : GOOGLEAPI.MAX_PAGE_SIZE, pageToken);
	}

	/**
	 * List files of a given folder tree.
	 * 
	 * @param folderStructure
	 * @param defaultPageSize
	 * @param pageToken
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	DriveFileList doGetFolderFiles(LinkedList<String> folderStructure, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException;

	/**
	 * Refresh credentials before listing files of a given folder tree by fileName filter.
	 * 
	 * @param folderStructure
	 * @param fileNameFilter
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	default DriveFileList getFolderFilesByFileNameFilter(LinkedList<String> folderStructure, String fileNameFilter) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		return this.getFolderFilesByFileNameFilter(folderStructure, fileNameFilter, GOOGLEAPI.DEFAULT_PAGE_SIZE, null);
	}
	
	/**
	 * Refresh credentials before listing files of a given folder tree by fileName filter.
	 * 
	 * @param folderStructure
	 * @param fileNameFilter
	 * @param pageSize
	 * @param pageToken
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	default DriveFileList getFolderFilesByFileNameFilter(LinkedList<String> folderStructure, String fileNameFilter, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException {
		this.refreshCredentials();
		return this.getFolderFilesByFileNameFilter(folderStructure, fileNameFilter, pageSize <= GOOGLEAPI.MAX_PAGE_SIZE ? pageSize : GOOGLEAPI.MAX_PAGE_SIZE, pageToken);
	}
	
	/**
	 * List files of a given folder tree by fileName filter.
	 * 
	 * @param folderHierarchy
	 * @param fileNameFilter
	 * @param pageSize
	 * @param pageToken
	 * @return {@link DriveFileList}
	 * @throws GoogleApiGeneralErrorException
	 */
	DriveFileList doGetFolderFilesByFileNameFilter(LinkedList<String> folderHierarchy, String fileNameFilter, Integer pageSize, String pageToken) throws GoogleApiGeneralErrorException;
	
}
