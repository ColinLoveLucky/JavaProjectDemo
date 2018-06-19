package com.qf.cobra.pojo;

import java.util.Map;

import com.qf.cobra.pojo.LoanApply.AppStatusEnum;

public class LoanApply {
	private String appStatus;// 进件状态
	private String timestamp;
	private String tenantId;
	private String clientId;
	private String appId;
	private String transactionId;
	private String policyId;
	private Map<String, Object> loanData;
	public String processInstanceId;
	public String processDefId;
	private String userId;
	private String channel;
	private String taskId;

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Map<String, Object> getLoanData() {
		return loanData;
	}

	public void setLoanData(Map<String, Object> loanData) {
		this.loanData = loanData;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	@Override
	public String toString() {
		return "LoanApply [appId=" + appId + ", transactionId=" + transactionId + ", policyId=" + policyId
				+ ", loanData=" + loanData + "]";
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public enum AppStatusEnum {
		LOANAPPLY_RULE_SUCCESS("LOANAPPLY_RULE_SUCCESS","预审通过"),LOANAPPLY_RULE_REJECT("LOANAPPLY_RULE_REJECT","预审拒绝"),
		STORE_REJECT("STORE_REJECT", "门店经理拒绝"),WAIT_COMPLEMENT("WAIT_COMPLEMENT", "待补全"), COMPLEMENTING("COMPLEMENTING", "补全中"),
		WAIT_FIRST_AUDIT("WAIT_FIRST_AUDIT","待初审"), FIRST_AUDITING("FIRST_AUDITING", "初审中"),
		FIRST_AUDIT_PASS("FIRST_AUDIT_PASS","初审通过"), FIRST_AUDIT_REJECT("FIRST_AUDIT_REJECT", "初审拒绝"),
		FINAL_AUDITING("FINAL_AUDITING","终审中"), FINAL_AUDIT_PASS("FINAL_AUDIT_PASS", "终审通过"), ANCEL_LOANAPP("CANCEL_LOANAPP", "取消借款"),
		FINAL_AUDIT_REJECT("FINAL_AUDIT_REJECT", "终审拒绝"), PAYMENT_STATUS_SUCCESS("PAYMENT_STATUS_SUCCESS", "放款成功"),
		PAYMENT_STATUS_FAIL("PAYMENT_STATUS_FAIL", "未放款");
		
		private String code;
		private String message;

		private AppStatusEnum(String code, String message) {
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
