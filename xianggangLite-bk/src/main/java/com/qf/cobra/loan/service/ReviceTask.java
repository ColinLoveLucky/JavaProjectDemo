package com.qf.cobra.loan.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanBpmRelation;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;

@SuppressWarnings("rawtypes")
public abstract class ReviceTask {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	@Autowired
	private MongoOperate mongoTemplate;
	@Autowired
	private LoanService loanService;
	
	
	@SuppressWarnings("unchecked")
	protected String getAppId(String processInstanceId){
		String appId = "";
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("processInstanceId", processInstanceId);
		LoanBpmRelation loanBpmRelation = (LoanBpmRelation) mongoTemplate.findOne(queryParam,
				LoanBpmRelation.class);
		if (loanBpmRelation == null
				|| StringUtils.isBlank(loanBpmRelation.getAppId())) {
			log.info("流程编号:{} 未获的相应的进件编号", processInstanceId);
		} else {
			appId = loanBpmRelation.getAppId();
			log.info("流程编号:{}相应的进件编号:{}", processInstanceId, appId);
		}
		return appId;
	}
	
	@SuppressWarnings("unchecked")
	private void receiveTask(TaskNodeInfo taskNodeInfo) {
		taskNodeInfo.setCompeleteStatus(DictItem.COMPELETE_STATUS_UNDO);
		mongoTemplate.save(taskNodeInfo);
	}
	
	protected void compeleteTask(TaskNodeInfo taskNodeInfo){
		
	}
	
	public void excute(TaskNodeInfo taskNodeInfo){
		String appId = getAppId(taskNodeInfo.getProcessInstanceId());
		
		if(StringUtils.isNotBlank(appId)){
			taskNodeInfo.setAppId(appId);
			receiveTask(taskNodeInfo);
			
			Boolean isSuccess = false;
			if(StringUtils.isNotBlank(appId)){
				isSuccess = excute(appId);
			}
			if(isSuccess){
				compeleteTask(taskNodeInfo);
			}
		}else{
			log.error("接收任务失败,未找到进件编号.taskNodeInfo:{}", JsonUtil.convert(taskNodeInfo));
		}
	}

	protected Boolean excute(String appId){
		return true;
	}
}
