package com.google.drive.api.exception;

/**
 * Exception class for Google API http general errors.
 * 
 * @author leonardo.ramos
 *
 */
public class GoogleApiGeneralErrorException extends GoogleApiException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public GoogleApiGeneralErrorException(String message) {
        super(message);
    }
	
	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public GoogleApiGeneralErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}