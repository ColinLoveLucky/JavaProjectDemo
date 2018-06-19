package com.qf.cobra.util;

public enum LoanAuditOperation {

	/**
	 * 进件录入
	 */
	LOAN_INPUT("LOAN_INPUT"),
	/**
	 * 进件录入-文件上传
	 */
	LOAN_INPUT_UPLOAD_IMAGES("LOAN_INPUT_UPLOAD_IMAGE"),

	/**
	 * 进件录入-文件删除
	 */
	LOAN_INPUT_DELETE_IMGES("LOAN_INPUT_DELETE_IMAGE"),
	/**
	 * 进件规则拒绝
	 */
	LOANAPPLY_RULE_REJECT("LOANAPPLY_RULE_REJECT"),
	/**
	 * 进件规则通过
	 */
	LOANAPPLY_RULE_SUCCESS("LOANAPPLY_RULE_SUCCESS"),
	/**
	 * 初审暂存
	 */
	FIRST_SAVE("FIRST_SAVE"),
	/**
	 * 初审提交
	 */
	FIRST_AUDIT("FIRST_AUDIT"),
	/**
	 * 终审回退
	 */
	FINAL_AUDIT_FALLBACK("FINAL_AUDIT_FALLBACK"),
	
	/**
	 * 门店拒绝
	 */
	FINAL_STORE_REJECT("FINAL_STORE_REJECT"),
	/**
	 * 终审提交
	 */
	FINAL_AUDIT("FINAL_AUDIT");

	private String value;

	private LoanAuditOperation(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
