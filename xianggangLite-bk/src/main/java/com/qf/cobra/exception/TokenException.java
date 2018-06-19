package com.qf.cobra.exception;

import com.qf.cobra.util.ResponseCode;

public class TokenException extends ServiceException {

	private static final long serialVersionUID = 1L;

	
	public TokenException(String message) {
		super(ResponseCode.TOKEN_INVALID,message);
	}
	
}
