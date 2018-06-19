package com.qf.cobra.loan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.pojo.Dict;
import com.qf.cobra.system.service.IDictService;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
public class DictController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);
	
	@Autowired
	private IDictService dictService;
	
	@PostMapping("/dict/create")
	public ResponseData create(@RequestBody Dict dict) {
		ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
		try {
			dictService.saveDict(dict);
		} catch (Exception e) {
			LOGGER.error("字典添加失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("字典添加失败");
		}
		return responseData;
	}
	
	@GetMapping("/dict/{code}")
	public ResponseData<Dict> create(@PathVariable("code") String code) {
		ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
		try {
			responseData.setData(dictService.getDict(code));
		} catch (Exception e) {
			LOGGER.error("字典获取失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("字典获取失败");
		}
		return responseData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/dict")
	public ResponseData getDict() {
		ResponseData responseData = ResponseUtil.defaultResponse();
		try {
			responseData.setData(dictService.findAll());
		} catch (Exception e) {
			LOGGER.error("字典获取失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("字典获取失败");
		}
		return responseData;
	}
	
	@GetMapping("/dict/refresh/{key}")
	public ResponseData refreshDict(@PathVariable("key") String key) {
		LOGGER.info("刷新字典：{}", new Object[]{key});
		ResponseData responseData = ResponseUtil.defaultResponse();
		try {
			dictService.refreshDict(key);
			LOGGER.info("刷新字典成功：{}", new Object[]{key});
		} catch (Exception e) {
			LOGGER.info("刷新字典失败：{}", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("刷新字典失败！");
		}
		return responseData;
	}
	
}
