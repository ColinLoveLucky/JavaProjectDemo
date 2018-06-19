package com.qf.cobra.pojo;

import com.qf.cobra.util.DictItem;

public class TaskNodeInfo {
	private String processInstanceId;
	private String taskId;
	private String taskName;
	private String taskDefinitionKey;
	private String appId;
	private String compeleteStatus;
	private String serviceCode;

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCompeleteStatus() {
		return compeleteStatus;
	}

	public void setCompeleteStatus(String compeleteStatus) {
		this.compeleteStatus = compeleteStatus;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public enum ReceiveServiceEnum {
		LOAN_APPLY_RULE("LOAN_APPLY_RULE", "进件规则", DictItem.POLICY_CODE_APPLY),
		NON_MOBILE_RULE("NON_MOBILE_RULE", "非运营商规则", DictItem.POLICY_CODE_NON_MOBILE), 
		CALL_REPORT_POLICY_RULE("CALL_REPORT_POLICY_RULE", "通话详单决策规则",DictItem.POLICY_CODE_DECIDE), 
//		MOBILE_AUTH_START("MOBILE_AUTH_START","通知app运营商认证授权"), 
		PUSH_QAPP("PUSH_QAPP", "推送进件信息至Qapp"), 
		OVER_DUE("OVER_DUE", "初审超时处理"),
		////////////////
		BORROWER_LOAN_APPLY_RULE("BORROWER_LOAN_APPLY_RULE", "borrower进件规则",DictItem.POLICY_BORROWER_LOAN_APPLY),
		BORROWER_LOAN_APPLY_DETAIL_LY("BORROWER_LOAN_APPLY_DETAIL_LY", "borrower进件详情岭勇录入"),
		BORROWER_LOAN_APPLY_PUSH_NCOBRA("BORROWER_LOAN_APPLY_PUSH_NCOBRA", "borrower进件信息同步ncobra"),
		BORROWER_LOAN_APPLY_PUSH_BID("BORROWER_LOAN_APPLY_PUSH_BID", "borrower进件信息同步bid")
		;

		private String serviceCode;
		private String serviceName;
		private String policyCode;

		public String getServiceCode() {
			return serviceCode;
		}

		public void setServiceCode(String serviceCode) {
			this.serviceCode = serviceCode;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getPolicyCode() {
			return policyCode;
		}

		public void setPolicyCode(String policyCode) {
			this.policyCode = policyCode;
		}

		private ReceiveServiceEnum(String serviceCode, String serviceName) {
			this.serviceCode = serviceCode;
			this.serviceName = serviceName;
		}

		private ReceiveServiceEnum(String serviceCode, String serviceName, String policyCode) {
			this(serviceCode, serviceName);
			this.policyCode = policyCode;
		}
		
		public static ReceiveServiceEnum getEnumByPolicyCode(String policyCode){
			for (ReceiveServiceEnum receiveServiceEnum : ReceiveServiceEnum.values()) {
				if(policyCode.equals(receiveServiceEnum.getPolicyCode())){
					return receiveServiceEnum;
				}
			}
			return null;
		}
		
	}
	
}
