package com.google.drive.api.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity responsible to represent a list of files retrieved from Google Drive and the metadata token.
 * 
 * @author leonardo.ramos
 *
 */
public class DriveFileList {
	
	private List<DriveFile> driveFiles;
	private String pageToken;
	
	/**
	 * Default constructor.
	 */
	public DriveFileList() {}
	
	/**
	 * Builder constructor.
	 * 
	 * @param builder
	 */
	public DriveFileList(DriveFileListBuilder driveFileListBuilder) {
		this.driveFiles = driveFileListBuilder.driveFiles;
		this.pageToken = driveFileListBuilder.pageToken;
	}
	
	/**
	 * Return the driveFiles.
	 * 
	 * @return driveFiles
	 */
	public List<DriveFile> getDriveFiles() {
		if (driveFiles == null) {
			driveFiles = new ArrayList<>();
		}
		return driveFiles;
	}

	/**
	 * Set the driveFiles.
	 * 
	 * @param driveFiles
	 */
	public void setDriveFiles(List<DriveFile> driveFiles) {
		this.driveFiles = driveFiles;
	}

	/**
	 * Return the pageToken.
	 * 
	 * @return pageToken
	 */
	public String getPageToken() {
		return pageToken;
	}

	/**
	 * Set the pageToken.
	 * 
	 * @param pageToken
	 */
	public void setPageToken(String pageToken) {
		this.pageToken = pageToken;
	}

	/**
	 * Builder pattern to build an instance of {@link DriveFileListBuilder}.
	 * 
	 * @return {@link DriveFileListBuilder}
	 */
	public static DriveFileListBuilder builder() {
		return new DriveFileListBuilder();
	} 
	
	/**
	 * Builder pattern inner class to build a new instance of {@link DriveFileList}.
	 * 
	 * @author leonardo.ramos
	 *
	 */
	public static class DriveFileListBuilder {
		
		private List<DriveFile> driveFiles;
		private String pageToken;
		
		/**
		 * Set the driveFiles to builder.
		 * 
		 * @param driveFiles
		 * @return {@link DriveFileListBuilder}
		 */
		public DriveFileListBuilder driveFiles(List<DriveFile> driveFiles) {
			this.driveFiles = driveFiles;
			return this;
		}
		
		/**
		 * Set the pageToken to builder.
		 * 
		 * @param pageToken
		 * @return {@link DriveFileListBuilder}
		 */
		public DriveFileListBuilder pageToken(String pageToken) {
			this.pageToken = pageToken;
			return this;
		}
		
		/**
		 * Build an instance of {@link DriveFileList}.
		 * 
		 * @return {@link DriveFileList}
		 */
		public DriveFileList build() {
			return new DriveFileList(this);
		}
	}

	/**
	 * DriveFileList toString.
	 *
	 * @return toString
	 */
	@Override
	public String toString() {
		return "DriveFileList [driveFiles=" + driveFiles + ", pageToken=" + pageToken + "]";
	}

}
