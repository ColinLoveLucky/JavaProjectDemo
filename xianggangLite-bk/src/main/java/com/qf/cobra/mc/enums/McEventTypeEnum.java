package com.qf.cobra.mc.enums;

/**
 * MC消息事件类型枚举
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:30
 */
public enum McEventTypeEnum {

	REGISTER("注册"),
	REAL_NAME("实名"),
	PRE_AUDIT("预审"),
	FINAL_AUDIT("终审");
	
	private McEventTypeEnum(String remark) {
		this.remark = remark;
	}
	
	private String remark;

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	
}
