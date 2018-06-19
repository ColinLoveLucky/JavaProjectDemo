package com.qf.cobra.loan.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.TaskNodeStatusService;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RequestMapping("/loanTaskStatus")
@RestController
@SuppressWarnings({"rawtypes","unchecked"})
public class TaskNodeStatusController {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(TaskNodeStatusController.class);
	
	@Autowired
	private TaskNodeStatusService taskNodeStatusService;
	
	@GetMapping("/queryLoanTaskNodeStatus/{appId}")
	public ResponseData queryLoanTaskNodeStatus(HttpServletRequest request,@PathVariable("appId") String appId){
		LOGGER.info("查询进件状态, 参数:{}", appId);
		ResponseData responseData=ResponseUtil.defaultResponse();
		try {
			responseData.setData(taskNodeStatusService.queryTaskNodeStatus(appId));
		} catch (Exception e) {
			LOGGER.error("查询进件状态异常,异常信息为:{}",e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

}
