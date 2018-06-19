package com.qf.cobra.loan.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.ITaskService;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
@RequestMapping("/task")
public class TaskController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
	private ITaskService taskService;
	
	@PostMapping("/taskPool")
	public ResponseData<Pagination> queryTaskPool(@RequestBody Pagination pagination) {
		ResponseData<Pagination> responseData = ResponseUtil.defaultResponse();
		try {
			taskService.queryTaskPool(pagination);
			responseData.setData(pagination);
		} catch (Exception e) {
			LOGGER.error("任务池查询失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("任务池查询失败");
		}
		return responseData;
	}
	
	@PostMapping("/userTask")
	public ResponseData<Pagination> queryUserTask(@RequestBody Pagination pagination) {
		ResponseData<Pagination> responseData = ResponseUtil.defaultResponse();
		try {
			taskService.queryUserTask(pagination);
			responseData.setData(pagination);
		} catch (Exception e) {
			LOGGER.error("待办任务查询失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("待办任务查询失败");
		}
		return responseData;
	}
	
	@PostMapping("/history")
	public ResponseData<Pagination> queryHistoryTask(@RequestBody Pagination pagination) {
		ResponseData<Pagination> responseData = ResponseUtil.defaultResponse();
		try {
			taskService.queryHistoryTask(pagination);
			responseData.setData(pagination);
		} catch (Exception e) {
			LOGGER.error("历史进件查询失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("历史借款查询失败");
		}
		return responseData;
	}
	
	@PostMapping("/claimTask")
	public ResponseData<Pagination> claimTask(@RequestBody Map<String, String> claimMap) {
		ResponseData<Pagination> responseData = ResponseUtil.defaultResponse();
		try {
			taskService.claimTask(claimMap);
		} catch (Exception e) {
			LOGGER.error("签收任务失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}
	
}
