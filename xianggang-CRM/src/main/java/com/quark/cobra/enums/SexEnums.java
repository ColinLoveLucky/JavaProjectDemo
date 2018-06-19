package com.quark.cobra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 性别枚举
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 13:01
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SexEnums {
	
	genderM("男"),
	genderF("女");
	
	/** 标签 */
	private String label;
	
}
