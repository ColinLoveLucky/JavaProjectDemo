package com.qf.cobra.loan.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.qf.cobra.loan.service.IPbocQueryService;
import com.qf.cobra.util.JsonUtil;
@Service
public class PbocQueryServiceImpl implements IPbocQueryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PbocQueryServiceImpl.class);
	@Value("${QC.PbocQueryPhone.url}")
	private String pullCallRecordsUrl;
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 获取通话详单
	 * @param requestJson
	 * {"GetPbocPhone": {"phoneNo": "18664660347","beginRow":
	 * "2","endRow": "4","sortField": "START_TIME","sortWay":
	 * "DESC","idCard": "440582199606086713"}}
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> myPullPhoneRecords(String requestJson) throws Exception{
		LOGGER.info("myPullPhoneRecords方法，拉取通话记录参数:{}", requestJson);
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		String postForObject = restTemplate.postForObject(pullCallRecordsUrl, entity, String.class);
		Map<String, Object> requestMap = JsonUtil.convert(postForObject, Map.class);
		Map<String, Object> phone = (Map<String, Object>) MapUtils.getObject(requestMap, "phone");
		List<Object> lists = (List<Object>) phone.get("detail");
		LOGGER.info("myPullPhoneRecords方法结束拉取通话记录返回结果:{}", lists);
		return lists;
	}

}
