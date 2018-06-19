package com.quark.cobra.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * DAC系统实名认证返回结果
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 12:52
 */
@Getter
@Setter
public class DacRealNameAuthResult<T> {
	
	/** 请求成功响应码 */
	public static final int SUCCESS_CODE = 200;

	/** 响应码 */
	private int code;
	
	/** 响应信息 */
	private String msg;
	
	/** 响应数据 */
	private T data;
	
	public boolean isSuccess() {
		return SUCCESS_CODE == code;
	}
}
