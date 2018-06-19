package com.qf.cobra.loan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.IVersionConfigService;
import com.qf.cobra.pojo.ProcessVariablesConfig;
import com.qf.cobra.pojo.VersionConfig;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
public class VersionController {
	@Autowired
	private IVersionConfigService service;
	
	@Autowired
    DiscoveryClient discoveryClient;
    @RequestMapping("/rest/{name}")
    public String get(@PathVariable("name") String name) {
        if ("all".equals(name)) {
            return discoveryClient.getServices().toString();
        } else {
//          System.err.println(restTemplate.getForObject("http://" + name
//                  + "/info", String.class));
            return discoveryClient.getInstances(name).toString();
        }
    }
    
	@GetMapping("/versionconfig/refresh")
	public ResponseData refreshVersionConfig() {
		ResponseData responseData = ResponseUtil.defaultResponse();
		List<VersionConfig> list = service.loadVersionConfigFromDB();
		responseData.setData(list);
		return responseData;
	}

	@GetMapping("/processconfig/refresh")
	public ResponseData refreshProcessConfig() {
		ResponseData responseData = ResponseUtil.defaultResponse();
		List<ProcessVariablesConfig> list = service
				.loadProcessVariablesConfigFromDB();
		responseData.setData(list);
		return responseData;
	}

	@GetMapping("/processconfig/copy")
	public ResponseData copyProcessConfig(
			@RequestParam("from") String oldProcessDefId,
			@RequestParam("to") String newProcessDefId) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		List<ProcessVariablesConfig> list;
		try {
			list = service.copyProcessVariablesConfig(
					oldProcessDefId, newProcessDefId);
			responseData.setData(list);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}
}
