package com.qf.cobra.exception;


public class AuthOutOfBoundsException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private Integer errorCode;

	public AuthOutOfBoundsException( String message) {
		super(message);
	}

	public AuthOutOfBoundsException(Integer errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public AuthOutOfBoundsException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

	public AuthOutOfBoundsException(Integer errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
	
	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	
}
