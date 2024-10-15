package com.google.drive.api.exception;

/**
 * Exception class for Google API errors.
 * 
 * @author leonardo.ramos
 *
 */
public abstract class GoogleApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    protected GoogleApiException() {
        super();
    }

    /**
     * Constructor with message error param.
     * 
     * @param message
     */
    protected GoogleApiException(String message) {
        super(message);
    }

    /**
     * Constructor for message error and throwable cause for this exception.
     * 
     * @param message
     * @param cause
     */
    protected GoogleApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for throwable cause for this exception.
     * 
     * @param cause
     */
    protected GoogleApiException(Throwable cause) {
        super(cause);
    }
	
}