package com.qf.cobra.loan.service;

import java.util.List;

import com.qf.cobra.pojo.ProcessVariablesConfig;
import com.qf.cobra.pojo.VersionConfig;

public interface IVersionConfigService {
	/**
	 * @Description 加载版本配置信息至缓存
	 */
	List<VersionConfig> loadVersionConfigFromDB();	
	
	/**
	 * @Description 查找版本配置信息
	 * @param tenantId
	 * @param clientId
	 * @param appVersion
	 * @return
	 */
	VersionConfig queryVersionConfig(String tenantId, String clientId,String appVersion);
	
	/**
	 * @Description 加载流程交易变量信息至缓存
	 */
	List<ProcessVariablesConfig> loadProcessVariablesConfigFromDB();
	
	/**
	 * @Description 查询流程交易变量信息
	 * @param processDefId
	 * @param taskDefinitionKey
	 * @param action
	 * @return
	 */
	ProcessVariablesConfig queryProcessVariablesConfig(String processDefId, String taskDefinitionKey,String action);
	
	/**
	 * @Description 采用复制的方式新建流程交易变量配置信息
	 * @param oldProcessDefId
	 * @param newProcessDefId
	 * @return
	 * @throws Exception
	 */
	List<ProcessVariablesConfig> copyProcessVariablesConfig(String oldProcessDefId,String newProcessDefId) throws Exception;
}
