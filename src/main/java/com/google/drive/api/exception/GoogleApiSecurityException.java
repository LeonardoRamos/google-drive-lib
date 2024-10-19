package com.google.drive.api.exception;

/**
 * Exception class for Google API security errors.
 * 
 * @author leonardo.ramos
 *
 */
public class GoogleApiSecurityException extends GoogleApiException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public GoogleApiSecurityException(String message, Exception cause) {
        super(message, cause);
    }
	
	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public GoogleApiSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

}