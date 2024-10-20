package com.google.drive.api.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.DriveApiConstants;
import com.google.drive.api.DriveApiConstants.GOOGLEAPI;
import com.google.drive.api.DriveApiConstants.MSGERROR;
import com.google.drive.api.exception.GoogleApiSecurityException;
import com.google.drive.api.service.GoogleService;

import io.micrometer.common.util.StringUtils;

/**
 * Configuration class to manage Google service {@code Spring} {@code beans}.
 * 
 * @author leonardo.ramos
 * 
 */
@Configuration
public class GoogleServiceAutoConfig {

	/**
	 * Return a {@link GoogleCredentials} bean.
	 * 
	 * @return {@link GoogleCredentials}
	 */
	@Bean
	@ConditionalOnMissingBean
	public GoogleCredentials credentials(@Value(DriveApiConstants.GOOGLEAPI.CREDENTIALS_PATH) String credentialsPath) throws GoogleApiSecurityException {
		try {
			
			File credentials = this.getCredentialsFile(credentialsPath);
			
			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(credentials));
			googleCredentials.refreshIfExpired();
			googleCredentials.refreshAccessToken();
			
			return googleCredentials;
	
		} catch (IOException | URISyntaxException e) {
			throw new GoogleApiSecurityException(MSGERROR.GOOGLE_OAUTH2_ERROR, e);
		}
	}

	/**
	 * Get a jSON file with the Google Credentials whether from resources folder or any other specified path.
	 * 
	 * @param credentialsPath
	 * @return Google Credentials file
	 * @throws URISyntaxException
	 */
	private File getCredentialsFile(String credentialsPath) throws URISyntaxException {
		if (StringUtils.isBlank(credentialsPath)) {
			URL credentialsResource = GoogleService.class.getResource(GOOGLEAPI.CLIENT_SECRET);
			return new File(credentialsResource.toURI());
		
		}
		return new File(credentialsPath);
	}
	
	/**
	 * Return a {@link Drive} bean.
	 * 
	 * @param googleCredentials
	 * @param applicationName
	 * @return {@link Drive}
	 * @throws GoogleApiSecurityException
	 */
	@Bean
	@ConditionalOnMissingBean
	public Drive driveService(GoogleCredentials googleCredentials,
			@Value(DriveApiConstants.GOOGLEAPI.APPLICATION_NAME_PROP) String applicationName) throws GoogleApiSecurityException {
		try {
			return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), 
					GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(googleCredentials))
			        .setApplicationName(applicationName)
			        .build();
			
		} catch (GeneralSecurityException | IOException e) {
			throw new GoogleApiSecurityException(MSGERROR.GOOGLE_OAUTH2_ERROR, e);
		} 
	}
	
}