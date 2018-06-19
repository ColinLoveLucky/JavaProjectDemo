package com.qf.cobra.loan.report.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.report.base.ReportServiceBase;
import com.qf.cobra.util.JsonUtil;

/**
 * 聚信立报告数据
 */
@Service("reportJXLServiceImpl")
public class ReportJXLServiceImpl extends ReportServiceBase{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportJXLServiceImpl.class);
	
	
/*	@SuppressWarnings("unchecked")
	public String convertDetail(Map<String, Object> jsonMap,Map<String, Object> requestBody) throws Exception{
		LOGGER.info("进入聚信立互联网资讯报告convertDetail方法,入参:{}", requestBody);
		Map<String, Object> requestMap = (Map<String, Object>) MapUtils.getObject(jsonMap, "detail");
		if(requestMap.get("JSON_INFO") !=null){
			//将不同版本返回json的统一改为report_data
			requestMap.put("report_data", requestMap.get("JSON_INFO"));
			requestMap.remove("JSON_INFO");
		}
		Map<String, Object>  reportData = (Map<String, Object>)requestMap.get("report_data");
		List<Object>  contactList = (List<Object>) reportData.get("contact_list");
		List<Object> contacts = (List<Object>) requestBody.get("contacts");
		List<Object> lists = containList(contacts,contactList, "phone_num");
		reportData.put("contactsList", lists);
		LOGGER.info("获得聚信立互联网资讯报告convertDetail方法,转换返回值:{}", requestMap);
		return JsonUtil.convert(requestMap);
		LOGGER.info("获得聚信立互联网资讯报告convertDetail方法,转换返回值:{}", requestMap);
		return JsonUtil.convert(requestMap);

	}*/
	
	@Override
    @SuppressWarnings("unchecked")
	public String convertDetail(Map<String, Object> jsonMap,Map<String, Object> requestBody) throws Exception{
		LOGGER.info("进入聚信立互联网资讯报告convertDetail方法,入参:{}", requestBody);
		Map<String, Object> requestMap = (Map<String, Object>) MapUtils.getObject(jsonMap, "rawData");
		Map<String, Object> tempMap = null;
		String[] dataKey = String.valueOf(jsonMap.get("dataKey")).split("\\|");
		if(dataKey.length==2){
			if("SUMMARY_PUSH".equals(dataKey[1])){
				tempMap = (Map<String, Object>)MapUtils.getObject(requestMap, "JSON_INFO");
			}else if("SUMMARY_PULL".equals(dataKey[1])){
				tempMap = (Map<String, Object>)MapUtils.getObject(requestMap, "report_data");
			}
		}
		
		if(tempMap ==null){
			return "报告格式不正确!";
		}
		
		List<Object>  contactList = (List<Object>) tempMap.get("contact_list");
		List<Object> contacts = (List<Object>) requestBody.get("contacts");
		List<Object> lists = containList(contacts,contactList, "phone_num");
		tempMap.put("contactsList", lists);
		/*LOGGER.info("获得聚信立互联网资讯报告convertDetail方法,转换返回值:{}", requestMap);
		return JsonUtil.convert(requestMap);*/
		LOGGER.info("获得聚信立互联网资讯报告convertDetail方法,转换返回值:{}", tempMap);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("report_data", tempMap);
		return JsonUtil.convert(resultMap);
		
	}
}
