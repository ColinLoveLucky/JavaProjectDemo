package com.qf.cobra.util;

public class DictItem {
	public static final String YES = "YES";
	public static final String CHARSET_NAME = "UTF-8";

	public static String COMPELETE_STATUS_UNDO = "0";
	public static String COMPELETE_STATUS_DONE = "1";
	// LoanApp字段
	public static final String LOANAPP_APPID = "appId";
	public static final String LOANAPP_POLICYID = "policyId";
	public static final String LOANAPP_TRANSACTIONID = "transactionId";
	public static final String LOANAPP_LOANDATA = "loanData";
	public static final String LOAN_TENANT = "tenantId";
	public static final String LOAN_CLIENT = "clientId";

	public static final String VARIABLES_NAME = "varLoanAppStatus";
	public static final String VARIABLES_VALUE_PREAUDIT_PASS = "1000";
	public static final String VARIABLES_VALUE_PREAUDIT_REJECT = "1001";
	public static final String VARIABLES_VALUE_PREAUDIT_REJECT_BALACK = "1100";
	public static final String VARIABLES_VALUE_PREAUDIT_REJECT_DUPLICATE = "1110";

	public static final String VARIABLES_VALUE_AUDIT_PASS = "5000";
	public static final String VARIABLES_VALUE_AUDIT_REJECT = "5001";
	
	public static final String BPMS_SUCCESS = "2000";

	public static final String SESSION_USER_PREFFIX = "BizApp-QYJ_SESSION_USER_";
	public static final String SESSION_TOKEN_PREFFIX = "BizApp-QYJ_SESSION_TOKEN_";

	public static final String PRODUCT_INTEREST_PREFFIX = "BizApp-QYJ_BWL_PRODUCT_CODE_";
	
	//录入
	public static final String TASK_INPUT = "UT_Material_Upload";
	//门店风险审核
	public static final String TASK_AUDIT_RISK = "UT_Risk_Audit";
	//终审
	public static final String TASK_AUDIT_FINAL = "UT_Last_Audit";
	//门店经理拒绝
	public static final String STORE_REJECT="STORE_REJECT";
	
	
	//通过
	public static final String PASS="PASS";
	
	//终审拒绝
	public static final String FINAL_REJECT_REASON="终审拒绝";
	
	//没有终审结果
	public static final String FINAL_NOT_RESULT="还未产生终审结果";
	
	//进件不存在
	public static final String NO_LOAN_APPLY="借款不存在";
	
	//拒绝
	public static final String REJECT="REJECT";
	
	//APP运营商认证标识
	public static final String QYJ_APP_CERTIFY = "QYJ_APP_CERTIFY";
	//进件拒件规则标识
	public static final String QYJ_LOANAPPLY_REJECT = "QYJ_LOANAPPLY_REJECT";
	//进件状态流转
    public static final String QYJ_LOANAPPLY_CHANGE = "QYJ_LOANAPPLY_CHANGE";
	
	public static final String QYJ_APP_CERTIFY_KEY="20003";
	
	public static final String STORE_REJECT_KEY="1001";
	
	//policy code	
	//进件规则QYJ_PH01
	public static final String POLICY_CODE_APPLY = "QYJ_PH01";
	//非运营商规则QYJ_B01 预审规则
	public static final String POLICY_CODE_NON_MOBILE = "QYJ_B01";
	//决策规则QYJ_T01 运营商规则
	public static final String POLICY_CODE_DECIDE = "QYJ_T01";
	//borrower进件规则BWL_QYJ_PH01
	public static final String POLICY_BORROWER_LOAN_APPLY = "BWL_QYJ_PH01";
	
	//渠道 岭勇
	public static final String APPLY_CHANNEL_BORROW = "Cobra";
	//渠道 borrow
	public static final String APPLY_CHANNEL_LY = "Cobra";
	
	//岭勇clientId
	public static final String CLIENT_LY = "client_qyj";
}
