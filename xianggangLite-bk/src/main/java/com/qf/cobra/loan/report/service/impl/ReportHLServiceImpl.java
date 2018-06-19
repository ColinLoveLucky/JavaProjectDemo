package com.qf.cobra.loan.report.service.impl;
import java.util.List;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.report.base.ReportServiceBase;
import com.qf.cobra.util.JsonUtil;

@Service("reportHLServiceImpl")
public class ReportHLServiceImpl extends ReportServiceBase{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportHLServiceImpl.class);
	
	
	@Override
    @SuppressWarnings("unchecked")
	public String convertDetail(Map<String, Object> jsonMap,Map<String, Object> requestBody) throws Exception{
		LOGGER.info("进入葫芦互联网资讯报告convertDetail方法,入参:{}", requestBody);
		Map<String, Object> requestMap = (Map<String, Object>) MapUtils.getObject(jsonMap, "detail");
		if(requestMap.get("push_data") !=null){
				requestMap.put("data", requestMap.get("push_data"));
				requestMap.remove("push_data");
		}
		Map<String, Object>  data = (Map<String, Object>)requestMap.get("data");
		List<Object>  contactList = (List<Object>) data.get("contact_list");
		List<Object> contacts = (List<Object>) requestBody.get("contacts");
		List<Object> lists = containList(contacts,contactList, "phone_num");
		data.put("contactsList", lists);
		LOGGER.info("获得葫芦互联网资讯报告convertDetail方法,转换返回值:{}", requestMap);
		return JsonUtil.convert(requestMap);
		
	}
	
}
