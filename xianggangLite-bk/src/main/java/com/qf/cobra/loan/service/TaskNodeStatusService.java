package com.qf.cobra.loan.service;

import java.util.Map;

public interface TaskNodeStatusService {
	/**
	 * 查询进件状态（调用bpms获取各个节点的状态）
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryTaskNodeStatus(String appId);
	
	

}
