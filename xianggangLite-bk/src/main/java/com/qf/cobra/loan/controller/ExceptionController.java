package com.qf.cobra.loan.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
@EnableConfigurationProperties({ ServerProperties.class })
public class ExceptionController implements ErrorController{

	private ErrorAttributes errorAttributes;

    @Autowired
    private ServerProperties serverProperties;


    /**
     * 初始化ExceptionController
     * @param errorAttributes
     */
    @Autowired
    public ExceptionController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "error")
	@ResponseBody
	public ResponseData<Map<String, Object>> error(
			HttpServletRequest request) {
		ResponseData<Map<String, Object>> resp = ResponseUtil.defaultResponse();
		resp.setCode(ResponseCode.SYSTEM_ERROR);
		resp.setMsg("error-Handler");
		resp.setData(getErrorAttributes(request,false));
		return resp;
	}


	/**
	 * 获取错误的信息
	 * 
	 * @param request
	 * @param includeStackTrace
	 * @return
	 */
	private Map<String, Object> getErrorAttributes(HttpServletRequest request,
			boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(
				request);
		return this.errorAttributes.getErrorAttributes(requestAttributes,
				includeStackTrace);
	}

	/**
	 * 是否包含trace
	 * 
	 * @param request
	 * @return
	 */
//	private boolean getTraceParameter(HttpServletRequest request) {
//		String parameter = request.getParameter("trace");
//		if (parameter == null) {
//			return false;
//		}
//		return !"false".equals(parameter.toLowerCase());
//	}

	/**
	 * 获取错误编码
	 * 
	 * @param request
	 * @return
	 */
//	private HttpStatus getStatus(HttpServletRequest request) {
//		Integer statusCode = (Integer) request
//				.getAttribute("javax.servlet.error.status_code");
//		if (statusCode == null) {
//			return HttpStatus.INTERNAL_SERVER_ERROR;
//		}
//		try {
//			return HttpStatus.valueOf(statusCode);
//		} catch (Exception ex) {
//			return HttpStatus.INTERNAL_SERVER_ERROR;
//		}
//	}

	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
