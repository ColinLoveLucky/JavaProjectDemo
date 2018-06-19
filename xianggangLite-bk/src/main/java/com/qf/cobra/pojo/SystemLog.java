package com.qf.cobra.pojo;

import java.util.Map;

/**
 * 
* @Title: SystemLog.java
* @Package com.quark.cobra.bizapp.pojo
* @Description: 系统操作日志
* @author ZiyangTan  
* @date 2017年4月27日 下午8:18:55
* @version V1.0
 */
public class SystemLog {

	private String userId;
	private String operate;
	private String timestamp;
	private Map<String, Object> extData;
	
	public SystemLog() {}
	
	public SystemLog(String userId, String operate, String timestamp) {
		this.userId = userId;
		this.operate = operate;
		this.timestamp = timestamp;
	}
	
	public SystemLog(String userId, String operate, String timestamp, Map<String, Object> extData) {
		this.userId = userId;
		this.operate = operate;
		this.timestamp = timestamp;
		this.extData = extData;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOperate() {
		return operate;
	}
	public void setOperate(String operate) {
		this.operate = operate;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public Map<String, Object> getExtData() {
		return extData;
	}
	public void setExtData(Map<String, Object> extData) {
		this.extData = extData;
	}
	
}
