package com.qf.cobra.pojo;

import java.util.Map;

/**
 * 
* @Title: LoanAuditHistory.java
* @Package com.quark.cobra.bizapp.pojo
* @Description: 进件操作历史表
* @author ZiyangTan  
* @date 2017年4月27日 下午8:14:43
* @version V1.0
 */
public class LoanAuditHistory {
	
	private String appId;
	private String userId;
	private String operate;
	private String timestamp;
	private Map<String, Object> bizData;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
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
	public Map<String, Object> getBizData() {
		return bizData;
	}
	public void setBizData(Map<String, Object> bizData) {
		this.bizData = bizData;
	}
	
}
