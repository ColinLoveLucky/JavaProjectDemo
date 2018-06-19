package com.qf.cobra.mc.enums;

/**
 * 客户端产品类型枚举
 * 
 * @author: XianjiCai
 * @date: 2018/04/09 10:50
 */
public enum McAppTypeEnum {

	QYJ("Q易借");
	
	private McAppTypeEnum(String remark) {
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
