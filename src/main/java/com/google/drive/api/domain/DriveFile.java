package com.google.drive.api.domain;

import java.util.Calendar;

import com.google.drive.api.DriveApiConstants.GOOGLEAPI;

/**
 * Entity responsible to represent a file retrieved from Google Drive.
 * 
 * @author leonardo.ramos
 *
 */
public class DriveFile {
	
	private String fileId;
	private String fileName;
	private String urlExport;
	private Calendar uploadDate;
	
	/**
	 * Default constructor.
	 */
	public DriveFile() {}
	
	/**
	 * Builder constructor.
	 * 
	 * @param builder
	 */
	public DriveFile(DriveFileBuilder driveFileBuilder) {
		this.setFileId(driveFileBuilder.fileId);

		this.fileName = driveFileBuilder.fileName;
		this.uploadDate = driveFileBuilder.uploadDate;
	}
	
	/**
	 * Return the fileId.
	 * 
	 * @return fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * Set the fileId.
	 * 
	 * @param fileId
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
		this.urlExport = new StringBuilder(GOOGLEAPI.DRIVE_BASE_EXPORT_URL).append(this.fileId).toString();
	}

	/**
	 * Return the fileName.
	 * 
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the fileName.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Return the urlExport.
	 * 
	 * @return urlExport
	 */
	public String getUrlExport() {
		return urlExport;
	}

	/**
	 * Return the uploadDate.
	 * 
	 * @return uploadDate
	 */
	public Calendar getUploadDate() {
		return uploadDate;
	}

	/**
	 * Set the uploadDate.
	 * 
	 * @param uploadDate
	 */
	public void setUploadDate(Calendar uploadDate) {
		this.uploadDate = uploadDate;
	}

	/**
	 * Builder pattern to build an instance of {@link DriveFileBuilder}.
	 * 
	 * @return {@link DriveFileBuilder}
	 */
	public static DriveFileBuilder builder() {
		return new DriveFileBuilder();
	} 
	
	/**
	 * Builder pattern inner class to build a new instance of {@link DriveFile}.
	 * 
	 * @author leonardo.ramos
	 *
	 */
	public static class DriveFileBuilder {
		
		private String fileId;
		private String fileName;
		private Calendar uploadDate;
		
		/**
		 * Set the fileId to builder.
		 * 
		 * @param fileId
		 * @return {@link DriveFileBuilder}
		 */
		public DriveFileBuilder fileId(String fileId) {
			this.fileId = fileId;
			return this;
		}
		
		/**
		 * Set the fileName to builder.
		 * 
		 * @param fileName
		 * @return {@link DriveFileBuilder}
		 */
		public DriveFileBuilder fileName(String fileName) {
			this.fileName = fileName;
			return this;
		}
		
		/**
		 * Set the uploadDate to builder.
		 * 
		 * @param uploadDate
		 * @return {@link DriveFileBuilder}
		 */
		public DriveFileBuilder uploadDate(Calendar uploadDate) {
			this.uploadDate = uploadDate;
			return this;
		}
		
		/**
		 * Build an instance of {@link DriveFile}.
		 * 
		 * @return {@link DriveFile}
		 */
		public DriveFile build() {
			return new DriveFile(this);
		}
	}

	/**
	 * DriveFile toString.
	 *
	 * @return toString
	 */
	@Override
	public String toString() {
		return "DriveFile [fileId=" + fileId + ", fileName=" + fileName + ", uploadDate=" + uploadDate + "]";
	}
	
}
