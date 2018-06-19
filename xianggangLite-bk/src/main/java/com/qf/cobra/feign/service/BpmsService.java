package com.qf.cobra.feign.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.bpms.service-id}")
public interface BpmsService {

	/**
	 * 启动一个任务流程
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.instance-uri}"
			, method = RequestMethod.POST
			, headers = { "Tenant=${system.tenantId}" })
	public String startProcess(Map<String, Object> params);

	/**
	 * 根据相应的action执行任务
	 * @param params action 具体操作 variable流程变量
	 * @param taskId 任务id
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.action-uri}/{taskID}"
			, method = RequestMethod.POST
			, headers = { "Tenant=${system.tenantId}" })
	public String actionProcess(Map<String, Object> params,
			@PathVariable("taskID") String taskId);

	/**
	 * 获取当前用户拥有访问权限的任务列表
	 * @param params useId 用户id 或者指定群组 groupId
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.action-uri}"
			, method = RequestMethod.GET
			, consumes = MediaType.APPLICATION_JSON_VALUE
			, headers = { "Tenant=${system.tenantId}" })
	public String queryList(@RequestParam Map<String, Object> params);

	/**
	 * 获取当前用户处理过的任务列表
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.query-history-uri}"
			, method = RequestMethod.GET
			, consumes = MediaType.APPLICATION_JSON_VALUE
			, headers = { "Tenant=${system.tenantId}" })
	public String queryHistoryList(@RequestParam Map<String, Object> params);

	/**
	 * 根据相应的processInstanceId和taskId执行接收任务
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.action-uri}"
			, method = RequestMethod.POST
			, headers = { "Tenant=${system.tenantId}" })
	public String compeleteReciveTask(Map<String, Object> params);

	/**
	 * 获取流程实例Id当前的状态
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@RequestMapping(value = "${feign.bpms.instance-uri}/{processInstanceId}"
			, method = RequestMethod.GET
			, consumes = MediaType.APPLICATION_JSON_VALUE
			, headers = { "Tenant=${system.tenantId}" })
	public String queryProcessInstance(@PathVariable("processInstanceId") String processInstanceId);

}
