package com.quark.cobra.Utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName: ResponseData
 * @author: [ChangcaiCao]
 * @CreateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateUser: [ChangcaiCao]
 * @UpdateDate: [2016年7月14日 上午11:09:56]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [响应数据实体类]
 * @version: [V1.0]
 */
@Getter
@Setter
@NoArgsConstructor
public class ResponseData {
	
	private static final String SUCCESS_MSG = "SUCCESS";
	
	/** 响应编码 */
    private int code;

    /** 响应消息 */
    private String msg;

    /** 响应数据 */
    private Object  data;

    private String token;
    
    protected ResponseData(String msg) {
		this.code = ResponseCode.SUCCESS;
		this.msg = msg;
	}
    
    public ResponseData(String msg, Object data) {
    	this.code = ResponseCode.SUCCESS;
    	this.msg = msg;
    	this.data = data;
    }
    
    public ResponseData(int code, String msg) {
    	this.code = code;
    	this.msg = msg;
    }
    
    /**
	 * 成功报文
	 *
	 * @return
	 */
	public static ResponseData ok() {
		return new ResponseData(SUCCESS_MSG);
	}
    
	/**
	 * 成功报文
	 *
	 * @param data
	 * @return
	 */
	public static ResponseData ok(Object data) {
		return new ResponseData(SUCCESS_MSG, data);
	}
	
	/**
	 * 创建失败报文
	 *
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResponseData error(int code, String msg) {
		return new ResponseData(code, msg);
	}

}
