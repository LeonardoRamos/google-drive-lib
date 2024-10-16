package com.google.drive.api;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementation of interface {@link ApplicationContextAware} to manage and retrieve
 * objects in {@code Spring} context.
 *
 * @author leonardo.ramos
 */
@SuppressWarnings("unchecked")
public final class ApplicationContexts implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContexts.applicationContext = applicationContext;
	}
	
	/**
	 * Return a Bean instance of the given class.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return T bean instance
	 */
	public static <T> T getBean(Class<T> clazz) {
		return ApplicationContexts.applicationContext.getBean(clazz);
	}
	
	/**
	 * Return a Bean instance of the given class.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return T bean instance
	 */
	public static <T> T getBean(String clazz) {
		return (T) ApplicationContexts.applicationContext.getBean(clazz);
	}
	
}