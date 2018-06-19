package com.qf.cobra.pojo;

import java.util.Map;

public class PushRecord {
	private String appId;
	private String timestamp;
	private Map<String, Object> pushData;
	private String pushResult;
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public Map<String, Object> getPushData() {
		return pushData;
	}
	public void setPushData(Map<String, Object> pushData) {
		this.pushData = pushData;
	}
	public String getPushResult() {
		return pushResult;
	}
	public void setPushResult(String pushResult) {
		this.pushResult = pushResult;
	}
}
