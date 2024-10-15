package com.google.drive.api.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.ApiConstants.GOOGLEAPI;
import com.google.drive.api.ApiConstants.MSGERROR;
import com.google.drive.api.exception.GoogleApiSecurityException;

/**
 * Interface base for any Google service with basic operations related to access and credentials.
 * 
 * @author leonardo.ramos
 *
 */
public interface GoogleService {
	
	/**
	 * Initialize the Google service.
	 * 
	 * @throws GoogleApiSecurityException
	 */
	void initializeService() throws GoogleApiSecurityException;
	
	/**
	 * Set the Google credentials.
	 * 
	 * @param credentials
	 */
	void setCredentials(GoogleCredentials credentials);

	/**
	 * Return the credentials.
	 * 
	 * @return {@link GoogleCredentials}
	 */
	GoogleCredentials getCredentials();
	
	/**
	 * Intitalize the credentials through json client secrets.
	 * 
	 * @throws GoogleApiSecurityException
	 */
	default void initializeCredentials() throws GoogleApiSecurityException {
		try {
			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(GOOGLEAPI.CLIENT_SECRET));
			googleCredentials.refreshIfExpired();
			
			this.setCredentials(googleCredentials);
			this.getCredentials().refreshAccessToken();
	
		} catch (IOException e) {
			throw new GoogleApiSecurityException(MSGERROR.GOOGLE_OAUTH2_ERROR, e);
		}
	}
	
	/**
	 * Refresh access credentials for Google service.
	 * 
	 * @throws GoogleApiSecurityException
	 */
	default void refreshCredentials() throws GoogleApiSecurityException {
		this.getRefreshedAccessToken();
	}
	
	/**
	 * Get the refreshed access token for the Google credentials.
	 * 
	 * @return {@ink AccessToken}
	 * @throws GoogleApiSecurityException
	 */
	default AccessToken getRefreshedAccessToken() throws GoogleApiSecurityException  {
		try {	
			if (!this.isCredentialInitialized()) {
				this.initializeCredentials();
			}
			
			this.getCredentials().refreshIfExpired();
			
			if (this.isAccessTokenExpired()) {
				return this.getCredentials().refreshAccessToken();
			}
			
			return this.getCredentials().getAccessToken();
			
		} catch (IOException e) {
			throw new GoogleApiSecurityException(MSGERROR.GOOGLE_OAUTH2_ERROR, e);
		}
	}

	/**
	 * Verify if access token has expired.
	 * 
	 * @return true if acess token has expired false otherwise
	 * @throws GoogleApiSecurityException
	 */
	default boolean isAccessTokenExpired() throws GoogleApiSecurityException {
		if (!this.isCredentialInitialized()) {
			return true;
		}
		
		Date now = Calendar.getInstance().getTime();
		Date expiration = this.getCredentials().getAccessToken().getExpirationTime();
		
		return expiration == null || expiration.after(now);
	}

	/**
	 * Verify if the Google credentials have been initialized.
	 * 
	 * @return true if the Google credentials have been initialized, false otherwise
	 */
	default boolean isCredentialInitialized() {
		return this.getCredentials() != null && this.getCredentials().getAccessToken() != null && 
				this.getCredentials().getAccessToken().getExpirationTime() != null;
	}

}