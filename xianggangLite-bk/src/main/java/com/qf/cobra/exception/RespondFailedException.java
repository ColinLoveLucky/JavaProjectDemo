package com.qf.cobra.exception;

import com.qf.cobra.util.ResponseCode;

public class RespondFailedException extends ServiceException {
	
	private static final long serialVersionUID = 1L;

	public RespondFailedException(Throwable cause) {
		super(ResponseCode.SYSTEM_ERROR,cause);
	}
}
