package com.qf.cobra.exception;


public class ServiceException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Integer errorCode;
	
	public ServiceException(Integer errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public ServiceException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

	public ServiceException(Integer errorCode,Throwable cause) {
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
