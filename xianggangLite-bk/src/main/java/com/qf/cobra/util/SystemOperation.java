package com.qf.cobra.util;

public enum SystemOperation {
	
	/**
	 * 登录
	 */
	LOGIN("LOGIN"),
	/**
	 * 注销
	 */
	LOGOUT("LOGOUT"),
	/**
	 * 签收任务
	 */
	CLAIM_TASK("CLAIM_TASK");
	
	private String value;

	private SystemOperation(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
