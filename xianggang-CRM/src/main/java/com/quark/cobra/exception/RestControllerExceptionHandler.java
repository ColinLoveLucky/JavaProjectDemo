package com.quark.cobra.exception;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.quark.cobra.Utils.ResponseCode;
import com.quark.cobra.Utils.ResponseData;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理拦截器
 * 针对系统发生异常,返回统一格式
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 13:09
 */
@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class RestControllerExceptionHandler {
	
	@ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseData handleException(BusinessException exception, HttpServletResponse response) {
        if (exception.getCause() != null) {
            log.error("{}:{}:{}", exception.getMessage(), exception.getStatus(), exception.getCause());
        }
        return ResponseData.error(exception.getStatus(), exception.getMessage());
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    ResponseData handleException(IllegalArgumentException exception) {
		log.error(exception.getMessage());
        return ResponseData.error(ResponseCode.SYSTEM_ERROR, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ResponseData handleException(HttpMessageNotReadableException exception) {
        log.error(exception.getMessage());
        return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, "输入的参数有误");
    }
	
	@ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseData handleException(Exception exception, HttpServletResponse response) {
    	log.error("未知错误", exception);
        if (exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) exception;
            return ResponseData.error(httpClientErrorException.getRawStatusCode(),
            		httpClientErrorException.getStatusCode().getReasonPhrase());
        } else {
        	return ResponseData.error(ResponseCode.SYSTEM_ERROR, exception.getMessage());
        }
    }
}
