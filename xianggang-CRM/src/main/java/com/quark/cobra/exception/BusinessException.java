package com.quark.cobra.exception;

import com.quark.cobra.constant.ErrorCode;

/**
 * 业务异常
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 13:08
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 2797607317809508352L;

	private int status;

	public BusinessException(String message) {
		this(ErrorCode.SYSTEM_ERROR_CODE, message);
	}

	public BusinessException(int status, String message) {
		super(message);
		this.status = status;
	}

	public BusinessException(int status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
