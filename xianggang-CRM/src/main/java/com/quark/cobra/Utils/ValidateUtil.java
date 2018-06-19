package com.quark.cobra.Utils;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * 验证参数工具类
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 14:38
 */
public final class ValidateUtil {

	/** private constructor */
	private ValidateUtil() {}
	
	/**
	 * 解析绑定参数校验结果,返回校验结果信息
	 * 
	 * @param result
	 * @return
	 */
	public static String processBindResult(BindingResult result) {
		if(result.hasErrors()) {
			List<ObjectError> errorList = result.getAllErrors();
			StringBuffer buffer = new StringBuffer();
			for(ObjectError error : errorList) {
				//buffer.append("\n" + error.getDefaultMessage());
				return error.getDefaultMessage();
			}
			return buffer.toString();
		}
		return null;
	}
}
