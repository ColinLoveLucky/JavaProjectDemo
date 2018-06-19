package com.quark.cobra.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 调用实名认证接口响应结果实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 12:49
 */
@Getter
@Setter
public class RealNameAuthRes {
	
	/** 查询状态 */
	private String queryStatus;
	
	/** 姓名 */
	private String identityName;
	
	/** 身份证号 */
	private String identityId;
	
	/** 描述 */
	private String description;

}
