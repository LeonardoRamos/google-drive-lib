package com.google.drive.api.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.google.api.services.drive.Drive;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.drive.api.exception.GoogleApiSecurityException;

/**
 * Configuration class to manage Google service {@code Spring} {@code beans} for testing purposes.
 * 
 * @author leonardo.ramos
 * 
 */
@Profile("test")
@TestConfiguration
public class MockedGoogleServiceAutoConfig {

	/**
	 * Return a mocked {@link GoogleCredentials} bean.
	 * 
	 * @return {@link GoogleCredentials}
	 */
	@Bean
	public GoogleCredentials credentials() throws GoogleApiSecurityException {
		return Mockito.mock(GoogleCredentials.class);
	}
	
	/**
	 * Return a mocked {@link Drive} bean.
	 * 
	 * @param googleCredentials
	 * @param applicationName
	 * @return {@link Drive}
	 * @throws GoogleApiSecurityException
	 */
	@Bean
	public Drive driveService() throws GoogleApiSecurityException {
		return Mockito.mock(Drive.class);
	}
	
}