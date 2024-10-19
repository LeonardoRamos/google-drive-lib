package com.google.drive.api.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.DriveApiConstants.MSGERROR;
import com.google.drive.api.exception.GoogleApiSecurityException;

/**
 * Interface base for any Google service with basic operations related to access and credentials.
 * 
 * @author leonardo.ramos
 *
 */
public interface GoogleService {
	
	/**
	 * Return the credentials.
	 * 
	 * @return {@link GoogleCredentials}
	 */
	GoogleCredentials getCredentials();
	
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
		Date now = Calendar.getInstance().getTime();
		Date expiration = this.getCredentials().getAccessToken().getExpirationTime();
		
		return expiration == null || expiration.after(now);
	}

}