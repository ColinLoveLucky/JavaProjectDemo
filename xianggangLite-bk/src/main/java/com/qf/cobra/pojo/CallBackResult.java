package com.qf.cobra.pojo;

public class CallBackResult {
	private String appId;
	private CallBackType type;
	private String remark;
	private String operation;
	private String timestamp;
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public CallBackType getType() {
		return type;
	}
	public void setType(CallBackType type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public enum CallBackType {
		CALL_BACK("CALL_BACK","回退");
		private String code;
		private String message;

		private CallBackType(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
	
}
