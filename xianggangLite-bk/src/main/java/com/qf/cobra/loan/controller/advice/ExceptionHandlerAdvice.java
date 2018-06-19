package com.qf.cobra.loan.controller.advice;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qf.cobra.exception.ServiceException;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExceptionHandlerAdvice.class);
	
	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public ResponseData<?> exceptionHandler(ServiceException e,
			HttpServletResponse response) {
		LOGGER.error("token失效", e);
		ResponseData<?> resp = ResponseUtil.defaultResponse();
		resp.setCode(e.getErrorCode());
		resp.setMsg(e.getMessage());
		return resp;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseData<?> exceptionHandler(Exception e,
			HttpServletResponse response) {
		LOGGER.error("系统异常", e);
		ResponseData<?> resp = ResponseUtil.defaultResponse();
		resp.setCode(ResponseCode.SYSTEM_ERROR);
		resp.setMsg("系统异常");
		return resp;
	}
}
