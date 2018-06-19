package com.qf.cobra.feign.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="${feign.ndes.service-id}")
public interface NdesService {
	 @RequestMapping(value="${feign.ndes.start-uri}"
			 ,method=RequestMethod.POST)
	 public String startProcess(Map<String,Object> params);
}
