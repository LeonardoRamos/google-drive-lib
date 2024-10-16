package com.google.drive.api.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.drive.api.ApplicationContexts;

/**
 * Configuration class to manage {@code Spring} {@code beans} basic to the application.
 * 
 * @author leonardo.ramos
 * 
 */
@Configuration
public class ApplicationContextConfig {

	/**
	 * Return an instance of {@link ApplicationContexts}.
	 * 
	 * @param applicationContext
	 * @return {@link ApplicationContexts}
	 */
	@Bean
	public ApplicationContexts applicationContexts(ApplicationContext applicationContext) {
		ApplicationContexts applicationContexts = new ApplicationContexts();
		applicationContexts.setApplicationContext(applicationContext);
		return applicationContexts;
	}
	
}