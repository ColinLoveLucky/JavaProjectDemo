package com.qf.cobra.mc.enums;

/**
 * 注册用户来源类型枚举
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:30
 */
public enum McSourceTypeEnum {

	LENDER("理财端"),
	BORROW("借款端");
	
	private McSourceTypeEnum(String remark) {
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
