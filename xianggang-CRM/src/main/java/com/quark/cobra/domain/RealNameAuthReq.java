package com.quark.cobra.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 调用实名认证接口接口实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 12:49
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealNameAuthReq {
	
	/** 查询系统 */
	private String querySystem;
	
	/** 查询用户 */
	private String queryUser;
	
	/** 姓名 */
	private String identityName;
	
	/** 身份证号 */
	private String identityId;

}
