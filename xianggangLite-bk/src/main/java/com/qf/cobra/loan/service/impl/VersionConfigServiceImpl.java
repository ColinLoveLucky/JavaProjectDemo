package com.qf.cobra.loan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.qf.cobra.loan.service.IVersionConfigService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.mongo.dao.ProcessVariablesConfigRepository;
import com.qf.cobra.mongo.dao.VersionConfigRepository;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.ProcessVariablesConfig;
import com.qf.cobra.pojo.VersionConfig;
import com.qf.cobra.util.JsonUtil;

@Service
public class VersionConfigServiceImpl implements IVersionConfigService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VersionConfigServiceImpl.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private VersionConfigRepository versionrepository;
	@Autowired
	private ProcessVariablesConfigRepository processrepository;
	@SuppressWarnings("rawtypes")
	@Autowired
	private MongoOperate mongoTemplate;

	@Override
	public List<VersionConfig> loadVersionConfigFromDB() {
		List<VersionConfig> list = versionrepository.findAll();
		if (CollectionUtils.isNotEmpty(list)) {
			stringRedisTemplate.opsForValue().set("versionConfig",
					JsonUtil.convert(list));
		} else {
			LOGGER.info("加载版本配置信息失败-无配置记录");
		}
		return list;
	}

	@Override
	public VersionConfig queryVersionConfig(String tenantId, String clientId,
			String appVersion) {
		VersionConfig versionConfig = null;
		if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(clientId)
				|| StringUtils.isBlank(appVersion)) {
			LOGGER.info(
					"获取版本配置信息失败-缺少查询参数.tenantId={},clientId={},appVersion={}",
					tenantId, clientId, appVersion);
		} else {
			VersionConfig versionConfigRedis = queryVersionConfigFromRedis(
					tenantId, clientId, appVersion);
			versionConfig = versionConfigRedis == null ? queryVersionConfigFromDB(
					tenantId, clientId, appVersion) : versionConfigRedis;
		}
		return versionConfig;
	}

	@SuppressWarnings("unchecked")
	public VersionConfig queryVersionConfigFromRedis(String tenantId,
			String clientId, String appVersion) {
		VersionConfig versionConfig = null;
		String json = stringRedisTemplate.opsForValue().get("versionConfig");
		if (StringUtils.isBlank(json)) {
			LOGGER.info(
					"获取版本配置信息失败-未找到相应的缓存.tenantId={},clientId={},appVersion={}",
					tenantId, clientId, appVersion);
		} else {
			List<Map<String,Object>> list = JsonUtil.convert(json, List.class);
			List<VersionConfig> targetList = new ArrayList<VersionConfig>();
			for(Map<String,Object> item:list){
				if(tenantId.equals(item.get("tenantId"))
						&&(clientId.equals(item.get("clientId")))
						&&(appVersion.equals(item.get("appVersion")))){
					targetList.add(JsonUtil.convert(JSONObject.toJSONString(item), VersionConfig.class));
				}
			}
			
			if (CollectionUtils.isNotEmpty(targetList)
					&& targetList.size() == 1) {
				versionConfig = targetList.get(0);
			} else {
				LOGGER.info(
						"获取版本配置信息失败-版本配置不正确.tenantId={},clientId={},appVersion={},resultSize={}",
						tenantId, clientId, appVersion, CollectionUtils
								.isNotEmpty(targetList) ? targetList.size() : 0);
			}
		}
		return versionConfig;
	}

	@SuppressWarnings("unchecked")
	public VersionConfig queryVersionConfigFromDB(String tenantId,
			String clientId, String appVersion) {
		VersionConfig versionConfig = null;

		Map<String, Object> query = new HashMap<String, Object>();
		query.put("tenantId", tenantId);
		query.put("clientId", clientId);
		query.put("appVersion", appVersion);

		List<VersionConfig> list = mongoTemplate.findAll(query,
				VersionConfig.class);
		if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
			versionConfig = list.get(0);
		} else {
			LOGGER.info(
					"获取版本配置信息失败-版本配置不正确.tenantId={},clientId={},appVersion={},resultSize={}",
					tenantId, clientId, appVersion,
					CollectionUtils.isNotEmpty(list) ? list.size() : 0);
		}
		return versionConfig;
	}

	@Override
	public List<ProcessVariablesConfig> loadProcessVariablesConfigFromDB() {
		List<ProcessVariablesConfig> list = processrepository.findAll();
		if (CollectionUtils.isNotEmpty(list)) {
			stringRedisTemplate.opsForValue().set("processConfig",
					JsonUtil.convert(list));
		} else {
			LOGGER.info("加载版本配置信息失败-无配置记录");
		}
		return list;
	}

	@Override
	public ProcessVariablesConfig queryProcessVariablesConfig(
			String processDefId, String taskDefinitionKey, String action) {
		ProcessVariablesConfig versionConfig = null;
		if (StringUtils.isBlank(processDefId) || StringUtils.isBlank(taskDefinitionKey)
				|| StringUtils.isBlank(action)) {
			LOGGER.info(
					"获取版本配置信息失败-缺少查询参数.processDefId={},taskDefinitionKey={},action={}",
					processDefId, taskDefinitionKey, action);
		} else {
			ProcessVariablesConfig versionConfigRedis = queryProcessVariablesConfigFromRedis(
					processDefId, taskDefinitionKey, action);
			versionConfig = versionConfigRedis == null ? queryProcessVariablesConfigFromDB(
					processDefId, taskDefinitionKey, action) : versionConfigRedis;
		}
		return versionConfig;
	}

	@SuppressWarnings("unchecked")
	public ProcessVariablesConfig queryProcessVariablesConfigFromRedis(
			String processDefId, String taskDefinitionKey, String action) {
		ProcessVariablesConfig versionConfig = null;
		String json = stringRedisTemplate.opsForValue().get("processConfig");
		if (StringUtils.isBlank(json)) {
			LOGGER.info(
					"获取流程变量配置信息失败-未找到相应的缓存.processDefId={},taskDefinitionKey={},action={}",
					processDefId, taskDefinitionKey, action);
		} else {
			List<ProcessVariablesConfig> list = JsonUtil.convert(json, List.class);
			List<ProcessVariablesConfig> targetList = list
					.stream()
					.filter(item -> StringUtils.equals(item.getProcessDefId(),
							processDefId)
							&& StringUtils.equals(item.getTaskDefinitionKey(), taskDefinitionKey)
							&& StringUtils.equals(item.getAction(),
									action)).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(targetList)
					&& targetList.size() == 1) {
				versionConfig = targetList.get(0);
			} else {
				LOGGER.info(
						"获取流程变量配置信息失败-版本配置不正确.processDefId={},taskDefinitionKey={},action={},resultSize={}",
						processDefId, taskDefinitionKey, action,
						CollectionUtils.isNotEmpty(list) ? list.size() : 0);
			}
		}
		return versionConfig;
	}

	@SuppressWarnings("unchecked")
	public ProcessVariablesConfig queryProcessVariablesConfigFromDB(String processDefId,
			String taskDefinitionKey, String action) {
		ProcessVariablesConfig versionConfig = null;

		Map<String, Object> query = new HashMap<String, Object>();
		query.put("processDefId", processDefId);
		query.put("taskDefinitionKey", taskDefinitionKey);
		query.put("action", action);

		List<ProcessVariablesConfig> list = mongoTemplate.findAll(query,
				ProcessVariablesConfig.class);
		if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
			versionConfig = list.get(0);
		} else {
			LOGGER.info(
					"获取流程变量配置信息失败-版本配置不正确.processDefId={},taskDefinitionKey={},action={},resultSize={}",
					processDefId, taskDefinitionKey, action,
					CollectionUtils.isNotEmpty(list) ? list.size() : 0);
		}
		return versionConfig;
	}

	@Override
	public List<ProcessVariablesConfig> copyProcessVariablesConfig(String oldProcessDefId,
			String newProcessDefId) throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("processDefId", newProcessDefId);
		List<ProcessVariablesConfig> target = mongoTemplate.findAll(query,
				ProcessVariablesConfig.class);
		if(CollectionUtils.isNotEmpty(target)){
			throw new Exception(String.format("流程版本%s已完成配置,无需重复配置", newProcessDefId));
		}
		query.put("processDefId", oldProcessDefId);
		List<ProcessVariablesConfig> source = mongoTemplate.findAll(query,
				ProcessVariablesConfig.class);
		if(CollectionUtils.isEmpty(source)){
			throw new Exception(String.format("流程版本%s未完成配置,请先完成该版本的配置", oldProcessDefId));
		}
		
		target = new ArrayList<ProcessVariablesConfig>();
		for (ProcessVariablesConfig processVariablesConfig : source) {
			processVariablesConfig.setId(null);
			processVariablesConfig.setProcessDefId(newProcessDefId);
			target.add(processVariablesConfig);
		}
		return processrepository.save(target);
	}

}
