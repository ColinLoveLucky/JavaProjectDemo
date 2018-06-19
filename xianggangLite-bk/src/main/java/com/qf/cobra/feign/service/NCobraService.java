package com.qf.cobra.feign.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "${feign.ncobra.service-id}")
public interface NCobraService {

	/**
	 * 同步进件信息到ncobra审核
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "${feign.ncobra.audit-uri}"
			, method = RequestMethod.POST
			, headers = { "tenantId=${feign.ncobra.tenantId}","clientId=${feign.ncobra.clientId}","productId=${feign.ncobra.productId}" })
	public String pushLoanApp(Object loanApp);

}
