package com.qf.cobra.pojo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qf.cobra.util.ObjectIdSerializer;


public class ProcessVariablesConfig {
	@Id
	@JsonSerialize(using = ObjectIdSerializer.class) 
	private ObjectId id;
	private String processDefId;
	private String action;
	private String taskDefinitionKey;
	private String processParams;	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getProcessDefId() {
		return processDefId;
	}
	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getProcessParams() {
		return processParams;
	}

	public void setProcessParams(String processParams) {
		this.processParams = processParams;
	}


	public enum TaskAction {
		START("start", "流程开启"), 
		CLAIM("claim", "签收"), 
		COMPLETE("complete", "完成"), 
		ROLLBACK("rollback", "回退"), 
		DELEGATE("delegate", "重新指派"), 
		RELEASE("release", "释放");

		private String code;
		private String message;

		private TaskAction(String code, String message) {
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

		@Override
		public String toString() {
			return this.code;
		}
	}

	public enum TaskDefinitionKey {
		START("start", "流程开启"), 
//		BANKCARD("RT_Not_Operator_Rule_Callback", "非运营商规则回到"), 
		UPLOAD("UT_Material_Upload", "资料上传/补全"), 
//		BANKCARD3("RT_Decision_Rule_Callback", "决策规则回调"), 
		RISK_AUDIT("UT_Risk_Audit", "门店风控审核"), 
		FINAL_AUDIT("UT_Last_Audit", "终审");

		private String code;
		private String message;

		private TaskDefinitionKey(String code, String message) {
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

		@Override
		public String toString() {
			return this.code;
		}
	}

}
