package com.google.drive.api;

public final class DriveApiConstants {
	
	private DriveApiConstants() {
		
	}
	
	public static final class GOOGLEAPI {
		
		private GOOGLEAPI() {
			
		}
		
		public static final String CLIENT_SECRET = "/client_secret.json";
		public static final String APPLICATION_NAME = "BirdexApi";
		public static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
		public static final String ID_FIELD_BASE = "id";
		public static final String FILE_FIELDS = "id, name, parents, createdTime";
		public static final String FILE_QUERY_IN_FOLDER = "mimeType!='application/vnd.google-apps.folder' and name='%s' and '%s' in parents";
		public static final String FILES_QUERY_IN_FOLDER = "mimeType!='application/vnd.google-apps.folder' and '%s' in parents";
		public static final String FOLDER_QUERY_NAME = "mimeType='application/vnd.google-apps.folder' and name='%s'";
		public static final String FOLDER_QUERY_PARENT = " and '%s' in parents";
		public static final String DRIVE_SPACES = "drive";
		public static final String DRIVE_BASE_EXPORT_URL = "https://drive.google.com/uc?export=view&id=";
		public static final String FOLDER_QUERY_FIELDS = "nextPageToken, files(" + FILE_FIELDS + ")";
		public static final String ANYONE_PERMISSION_TYPE = "anyone";
		public static final String READER_PERMISSION_TOLE = "reader";
		public static final Integer DEFAULT_PAGE_SIZE = 20;
		public static final Integer MAX_PAGE_SIZE = 100;
	}

	public static final class MSGERROR {
		
		private MSGERROR() {
			
		}
		
		public static final String DRIVE_GENERAL_ERROR = "Error while performing action in google drive service";
		public static final String DRIVE_UPLOAD_ERROR = "Error while performing upload action in google drive service, upload result null";
		public static final String GOOGLE_OAUTH2_ERROR = "Error while getting access token for google api connection to initialize service.";
	}

}
