package com.qf.cobra.loan.service;

import java.util.Map;

import com.qf.cobra.pojo.Pagination;

/**
 * 
* @Title: ITaskService.java
* @Package com.quark.cobra.bizapp.loan.service
* @Description: TODO(用一句话描述该文件做什么)
* @author ZiyangTan  
* @date 2017年4月27日 下午2:59:00
* @version V1.0
 */
public interface ITaskService {

	void queryTaskPool(Pagination pagination) throws Exception;
	
	void queryUserTask(Pagination pagination);
	
	void queryHistoryTask(Pagination pagination) throws Exception;
	
	void claimTask(Map<String, String> claimMap);
	
}
