package com.quark.cobra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 婚姻状况枚举
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 13:02
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaritalStatusEnums {
	
	marrageSingle("未婚"),
	marrageCouple("已婚"),
	marrageDivorced("离异"),
	marragelosecouple("丧偶"),
	marrageOther("其他");
	
	/** 标签 */
	private String label;
	
}
