package com.qf.cobra.mc.enums;

/**
 * 客户端注册来源枚举
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:30
 */
public enum McClientSourceEnum {

	WEB("官网"),
	MOBILE("移动端");
	
	private McClientSourceEnum(String remark) {
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
