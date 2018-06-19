package com.qf.cobra.loan.service;

import java.util.List;

public interface IPbocQueryService {

	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description 拉取通话记录
	 * @author JinbaoYang
	 * @param requestJson
	 *            {"GetPbocPhone": {"phoneNo": "18664660347","beginRow":
	 *            "2","endRow": "4","sortField": "START_TIME","sortWay":
	 *            "DESC","idCard": "440582199606086713"}}
	 * @return
	 * @since 2017年4月22日 下午2:49:11
	 */
	
	
	public List<Object> myPullPhoneRecords(String requestJson)  throws Exception;

}
