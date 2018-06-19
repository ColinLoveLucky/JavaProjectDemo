/**
 * @Title:   [ResponseData.java]
 * @Package: [com.qf.uca.util]
 * @author:  [ChangcaiCao] 
 * @CreateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateUser: [ChangcaiCao]   
 * @UpdateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description:  [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
package com.qf.cobra.util;

/**
 * @ClassName: ResponseData
 * @author:  [ChangcaiCao] 
 * @CreateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateUser: [ChangcaiCao]   
 * @UpdateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description:  [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
public class ResponseData<T> {

	/*
	 * 响应编码
	 */
	private int code;
	
	/*
	 * 响应消息
	 */
	private String msg;
	
	/*
	 * 返回的数据
	 */
	private T data;
	
	private String token;
	
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
