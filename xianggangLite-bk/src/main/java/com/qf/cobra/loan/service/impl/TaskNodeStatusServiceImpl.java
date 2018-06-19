package com.qf.cobra.loan.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.feign.service.BpmsService;
import com.qf.cobra.loan.service.TaskNodeStatusService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.util.JsonUtil;

@Service
@SuppressWarnings({"rawtypes","unchecked"})
public class TaskNodeStatusServiceImpl implements TaskNodeStatusService{
	
	@Autowired
	private MongoOperate mongoTemplate;
	
	@Autowired
	private BpmsService bpmsService;

	@Override
	public Map<String, Object> queryTaskNodeStatus(String appId) {
		Map<String, Object> resultMap=null;
		if(StringUtils.isNotBlank(appId)){
			Map<String, Object> queryParam=new HashMap<String, Object>();
			queryParam.put("appId", appId);
			//TaskNodeInfo taskNodeInfo =(TaskNodeInfo)mongoTemplate.findOne(queryParam, TaskNodeInfo.class);
			LoanApply loanApply =(LoanApply)mongoTemplate.findOne(queryParam, LoanApply.class);
			if(loanApply!=null){
				//调用bpms获取流程各个节点的状态
				String resultJson = bpmsService.queryProcessInstance(loanApply.getProcessInstanceId());
				resultJson = resultJson == null ? "" : resultJson;
				Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
				resultMap = MapUtils.getMap(resultObj, "responseBody");
			}
		}
		return resultMap;
	}

}
