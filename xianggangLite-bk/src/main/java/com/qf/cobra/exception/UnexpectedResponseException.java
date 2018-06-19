package com.qf.cobra.exception;

import com.qf.cobra.util.ResponseCode;

public class UnexpectedResponseException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public UnexpectedResponseException(String message,Throwable cause) {
		super(ResponseCode.SYSTEM_ERROR,message,cause);
	}
	
	public UnexpectedResponseException(String message) {
		super(ResponseCode.SYSTEM_ERROR,message);
	}
}
