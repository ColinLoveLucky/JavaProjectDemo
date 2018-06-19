package com.quark.cobra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 教育程度枚举
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 13:05
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EducationLevelEnums {
	
	educationA("硕士及以上"),
	educationB("本科"),
	educationC("大专"),
	educationD("高中及以下");
	
	/** 标签 */
	private String label;
	
}
