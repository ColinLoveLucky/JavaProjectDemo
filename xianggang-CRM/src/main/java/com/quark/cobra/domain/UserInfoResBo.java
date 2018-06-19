package com.quark.cobra.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户基本信息响应实体类
 * 返回给客户端
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 13:41
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResBo {
	
	/** 认证状态 */
	private String authStatus;
	
	/** 认证结果描述 */
	private String desc;
	
}
