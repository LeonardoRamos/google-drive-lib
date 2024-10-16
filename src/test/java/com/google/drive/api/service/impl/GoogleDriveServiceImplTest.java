package com.google.drive.api.service.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.DriveApiConstants.GOOGLEAPI;
import com.google.drive.api.domain.DriveFile;
import com.google.drive.api.domain.DriveFileList;
import com.google.drive.api.exception.GoogleApiGeneralErrorException;

@ActiveProfiles(profiles = "test")
class GoogleDriveServiceImplTest {

	private Drive driveService = Mockito.mock(Drive.class);
	private GoogleCredentials credentials = Mockito.mock(GoogleCredentials.class);
	private Files files = Mockito.mock(Files.class);
	private Create create = Mockito.mock(Create.class);
	private Get get = Mockito.mock(Get.class);
	private Permissions.Create createPermission = Mockito.mock(Permissions.Create.class);
	private Permissions permissions = Mockito.mock(Permissions.class);
	private com.google.api.services.drive.model.Permission modelPermission = Mockito.mock(com.google.api.services.drive.model.Permission.class);
	private List list = Mockito.mock(List.class);
	private List listParent = Mockito.mock(List.class);
	private List listChild = Mockito.mock(List.class);
	
	private GoogleDriveServiceImpl googleDriveService = new GoogleDriveServiceImpl(driveService, credentials);
	
	@Test
	void doGetFileByNameAndParentFolder_whenSuccess() throws IOException {
		String fileName = "fileTest";
		String fileId = "fileTestId";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderDataCreateChild(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		File expectedFile = new File();
		expectedFile.setId(fileId);
		expectedFile.setName(fileName);
		expectedFile.setCreatedTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		
		FileList expectedResult = new FileList();
		expectedResult.setFiles(Arrays.asList(expectedFile));
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILE_QUERY_IN_FOLDER, fileName, childFolderId))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FILE_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(expectedResult);
		
		Optional<DriveFile> result = this.googleDriveService.doGetFileByNameAndParentFolder(folderHierarchy, fileName);
		
		Assertions.assertTrue(result.isPresent());
		Assertions.assertEquals(result.get().getFileId(), expectedFile.getId());
		Assertions.assertEquals(result.get().getFileName(), expectedFile.getName());
		Assertions.assertEquals(result.get().getUrlExport(), GOOGLEAPI.DRIVE_BASE_EXPORT_URL + result.get().getFileId());
	}
	
	@Test
	void doGetFileByNameAndParentFolder_whenEmptyResult() throws IOException {
		String fileName = "fileTest";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderDataCreateChild(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILE_QUERY_IN_FOLDER, fileName, childFolderId))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FILE_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(null);
		
		Optional<DriveFile> result = this.googleDriveService.doGetFileByNameAndParentFolder(folderHierarchy, fileName);
		
		Assertions.assertTrue(result.isEmpty());
	}

	private LinkedList<String> mockFolderDataCreateChild(String parentFolderName, String parentFolderId, String childFolderName, String childFolderId) throws IOException {
		LinkedList<String> folderHierarchy = new LinkedList<>();
		folderHierarchy.add(parentFolderName);
		folderHierarchy.add(childFolderName);
		
		File expectedParentFile = new File();
		expectedParentFile.setId(parentFolderId);
		expectedParentFile.setName(parentFolderName);
		
		FileList expectedParentResult = new FileList();
		expectedParentResult.setFiles(Arrays.asList(expectedParentFile));
		
		StringBuilder queryParent = new StringBuilder(String.format(GOOGLEAPI.FOLDER_QUERY_NAME, parentFolderName));
		
		Mockito.when(this.list.setQ(queryParent.toString())).thenReturn(this.listParent);
		Mockito.when(this.listParent.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.listParent);
		Mockito.when(this.listParent.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.listParent);
		Mockito.when(this.listParent.setPageToken(null)).thenReturn(this.listParent);
		Mockito.when(this.listParent.execute()).thenReturn(expectedParentResult);
		
		StringBuilder queryChild = new StringBuilder(String.format(GOOGLEAPI.FOLDER_QUERY_NAME, childFolderName));
		queryChild.append(String.format(GOOGLEAPI.FOLDER_QUERY_PARENT, parentFolderId));
		
		Mockito.when(this.list.setQ(queryChild.toString())).thenReturn(this.listChild);
		Mockito.when(this.listChild.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.listChild);
		Mockito.when(this.listChild.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.listChild);
		Mockito.when(this.listChild.setPageToken(null)).thenReturn(this.listChild);
		Mockito.when(this.listChild.execute()).thenReturn(null);

		File expectedChildFile = new File();
		expectedChildFile.setId(childFolderId);
		expectedChildFile.setName(childFolderName);
		
		File fileMetadata = new File();
		fileMetadata.setName(childFolderName);
		fileMetadata.setMimeType(GOOGLEAPI.FOLDER_MIME_TYPE);
		fileMetadata.setParents(Collections.singletonList(parentFolderId));
		
		Mockito.when(this.files.create(fileMetadata)).thenReturn(create);
		Mockito.when(this.create.setFields(GOOGLEAPI.ID_FIELD_BASE)).thenReturn(create);
		Mockito.when(this.create.execute()).thenReturn(expectedChildFile);
		
		return folderHierarchy;
	}
	
	@Test
	void doDownloadFiler_whenSuccess() throws IOException {
		String fileId = "fileTestId";
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.get(fileId)).thenReturn(this.get);
		Mockito.doNothing().when(this.get).executeMediaAndDownloadTo(Mockito.any());
		
		Assertions.assertDoesNotThrow(() -> this.googleDriveService.doDownloadFile(fileId));
	}
	
	@Test
	void doGetFolderFiles_whenEmptyResults() throws IOException {
		Integer pageSize = 10;
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER, childFolderId))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.setPageSize(pageSize)).thenReturn(this.list);
		Mockito.when(this.list.setPageToken(null)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(null);
		
		DriveFileList result = this.googleDriveService.doGetFolderFiles(folderHierarchy, pageSize, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.getDriveFiles().isEmpty());
		Assertions.assertNull(result.getPageToken());
	}

	@Test
	void doGetFolderFiles_whenSuccess() throws IOException {
		Integer pageSize = 10;
		
		String fileName = "fileTest";
		String fileId = "fileTestId";
		String resultNextPageToken = "nextPageToken";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		File expectedFile = new File();
		expectedFile.setId(fileId);
		expectedFile.setName(fileName);
		expectedFile.setCreatedTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		
		FileList expectedResult = new FileList();
		expectedResult.setFiles(Arrays.asList(expectedFile));
		expectedResult.setNextPageToken(resultNextPageToken);
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER, childFolderId))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.setPageSize(pageSize)).thenReturn(this.list);
		Mockito.when(this.list.setPageToken(null)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(expectedResult);
		
		DriveFileList result = this.googleDriveService.doGetFolderFiles(folderHierarchy, pageSize, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertTrue(!result.getDriveFiles().isEmpty());
		Assertions.assertEquals(result.getPageToken(), resultNextPageToken);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getFileId(), fileId);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getFileName(), fileName);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getUrlExport(), GOOGLEAPI.DRIVE_BASE_EXPORT_URL + fileId);
	}
	
	@Test
	void doGetFolderFilesByFileNameFilter_whenSuccess() throws IOException {
		Integer pageSize = 10;
		
		String fileName = "fileTest";
		String fileId = "fileTestId";
		String fileNameFlter = "Test";
		String resultNextPageToken = "nextPageToken";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		File expectedFile = new File();
		expectedFile.setId(fileId);
		expectedFile.setName(fileName);
		expectedFile.setCreatedTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		
		FileList expectedResult = new FileList();
		expectedResult.setFiles(Arrays.asList(expectedFile));
		expectedResult.setNextPageToken(resultNextPageToken);
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER_FILENAME_CONTAINS, childFolderId, fileNameFlter))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.setPageSize(pageSize)).thenReturn(this.list);
		Mockito.when(this.list.setPageToken(null)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(expectedResult);
		
		DriveFileList result = this.googleDriveService.doGetFolderFilesByFileNameFilter(folderHierarchy, fileNameFlter, pageSize, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertTrue(!result.getDriveFiles().isEmpty());
		Assertions.assertEquals(result.getPageToken(), resultNextPageToken);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getFileId(), fileId);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getFileName(), fileName);
		Assertions.assertEquals(result.getDriveFiles().getFirst().getUrlExport(), GOOGLEAPI.DRIVE_BASE_EXPORT_URL + fileId);
	}
	
	@Test
	void doGetFolderFilesByFileNameFilter_whenEmptyResults() throws IOException {
		Integer pageSize = 10;
		
		String fileName = "fileTest";
		String fileId = "fileTestId";
		String fileNameFlter = "Test";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		File expectedFile = new File();
		expectedFile.setId(fileId);
		expectedFile.setName(fileName);
		expectedFile.setCreatedTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		Mockito.when(this.list.setQ(String.format(GOOGLEAPI.FILES_QUERY_IN_FOLDER_FILENAME_CONTAINS, childFolderId, fileNameFlter))).thenReturn(this.list);
		Mockito.when(this.list.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.list);
		Mockito.when(this.list.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.list);
		Mockito.when(this.list.setPageSize(pageSize)).thenReturn(this.list);
		Mockito.when(this.list.setPageToken(null)).thenReturn(this.list);
		Mockito.when(this.list.execute()).thenReturn(null);
		
		DriveFileList result = this.googleDriveService.doGetFolderFilesByFileNameFilter(folderHierarchy, fileNameFlter, pageSize, null);
		
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.getDriveFiles().isEmpty());
		Assertions.assertNull(result.getPageToken());
	}
	
	@Test
	void doUploadFileToFolder_whenSuccessPublicPermission() throws IOException {
		String fileName = "fileTest";
		String fileId = "fileTestId";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		File expectedFile = new File();
		expectedFile.setId(fileId);
		expectedFile.setName(fileName);
		expectedFile.setCreatedTime(new DateTime(Calendar.getInstance().getTimeInMillis()));
		
		Path path = java.nio.file.Files.createTempFile(fileName, ".txt");
		java.nio.file.Files.write(path, fileName.getBytes());
		java.io.File file = path.toFile();
		file.deleteOnExit();
		
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());
		fileMetadata.setParents(Collections.singletonList(childFolderId));

		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.create(Mockito.any(), Mockito.any())).thenReturn(this.create);
		Mockito.when(this.create.setFields(GOOGLEAPI.FILE_FIELDS)).thenReturn(this.create);
		Mockito.when(this.create.execute()).thenReturn(expectedFile);
		
		Permission userPermission = new Permission()
			    .setType(GOOGLEAPI.ANYONE_PERMISSION_TYPE)
			    .setRole(GOOGLEAPI.READER_PERMISSION_TOLE);
		
		Mockito.when(this.driveService.permissions()).thenReturn(this.permissions);
		Mockito.when(this.permissions.create(fileId, userPermission)).thenReturn(this.createPermission);
		Mockito.when(this.createPermission.setFields(GOOGLEAPI.ID_FIELD_BASE)).thenReturn(this.createPermission);
		Mockito.when(this.createPermission.execute()).thenReturn(this.modelPermission);
		
		DriveFile result = this.googleDriveService.doUploadFileToFolder(folderHierarchy, file, true);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getFileId(), fileId);
		Assertions.assertEquals(result.getFileName(), fileName);
		Assertions.assertEquals(result.getUrlExport(), GOOGLEAPI.DRIVE_BASE_EXPORT_URL + fileId);
	}
	
	@Test
	void doUploadFileToFolder_whenErrorInUpload() throws IOException {
		String fileName = "fileTest";
		
		String parentFolderName = "parentFolder";
		String parentFolderId = "parentFolderId";
		String childFolderName = "childFolder";
		String childFolderId = "childFolderId";
		
		LinkedList<String> folderHierarchy = this.mockFolderData(parentFolderName, parentFolderId, childFolderName, childFolderId);
		
		Path path = java.nio.file.Files.createTempFile(fileName, ".txt");
		java.nio.file.Files.write(path, fileName.getBytes());
		java.io.File file = path.toFile();
		file.deleteOnExit();
		
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());
		fileMetadata.setParents(Collections.singletonList(childFolderId));

		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.create(Mockito.any(), Mockito.any())).thenReturn(this.create);
		Mockito.when(this.create.setFields(GOOGLEAPI.FILE_FIELDS)).thenReturn(this.create);
		Mockito.when(this.create.execute()).thenReturn(null);
		
		Assertions.assertThrows(GoogleApiGeneralErrorException.class, () -> this.googleDriveService.doUploadFileToFolder(folderHierarchy, file, true));
	}
	
	private LinkedList<String> mockFolderData(String parentFolderName, String parentFolderId, String childFolderName, String childFolderId) throws IOException {
		Mockito.when(this.driveService.files()).thenReturn(this.files);
		Mockito.when(this.files.list()).thenReturn(this.list);
		
		LinkedList<String> folderHierarchy = new LinkedList<>();
		folderHierarchy.add(parentFolderName);
		folderHierarchy.add(childFolderName);
		
		File expectedParentFile = new File();
		expectedParentFile.setId(parentFolderId);
		expectedParentFile.setName(parentFolderName);
		
		FileList expectedParentResult = new FileList();
		expectedParentResult.setFiles(Arrays.asList(expectedParentFile));
		
		StringBuilder queryParent = new StringBuilder(String.format(GOOGLEAPI.FOLDER_QUERY_NAME, parentFolderName));
		
		Mockito.when(this.list.setQ(queryParent.toString())).thenReturn(this.listParent);
		Mockito.when(this.listParent.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.listParent);
		Mockito.when(this.listParent.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.listParent);
		Mockito.when(this.listParent.setPageToken(null)).thenReturn(this.listParent);
		Mockito.when(this.listParent.execute()).thenReturn(expectedParentResult);
		
		File expectedChildFile = new File();
		expectedChildFile.setId(childFolderId);
		expectedChildFile.setName(childFolderName);
		
		FileList expectedChildResult = new FileList();
		expectedChildResult.setFiles(Arrays.asList(expectedChildFile));
		
		StringBuilder queryChild = new StringBuilder(String.format(GOOGLEAPI.FOLDER_QUERY_NAME, childFolderName));
		queryChild.append(String.format(GOOGLEAPI.FOLDER_QUERY_PARENT, parentFolderId));
		
		Mockito.when(this.list.setQ(queryChild.toString())).thenReturn(this.listChild);
		Mockito.when(this.listChild.setSpaces(GOOGLEAPI.DRIVE_SPACES)).thenReturn(this.listChild);
		Mockito.when(this.listChild.setFields(GOOGLEAPI.FOLDER_QUERY_FIELDS)).thenReturn(this.listChild);
		Mockito.when(this.listChild.setPageToken(null)).thenReturn(this.listChild);
		Mockito.when(this.listChild.execute()).thenReturn(expectedChildResult);
		
		return folderHierarchy;
	}
	
}