/**   
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
* @Title: IQyjapiServiceImpl.java 
* @Package com.qf.cobra.qyjapi.service.impl 
* @Description:  管理APP运营商认证接口调用
* @author YabinLi  
* @date 2017年6月7日 下午3:30:49 
* @version V1.0   
*/
package com.qf.cobra.qyjapi.service.impl;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.netfinworks.common.lang.StringUtil;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.qyjapi.service.IQyjapiService;
import com.qf.cobra.rabbitMQ.MQBaseMessage;
import com.qf.cobra.rabbitMQ.RabbitProducer;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.HttpClientUtil;
import com.qf.cobra.util.JsonUtil;

/**
 * @ClassName: IQyjapiServiceImpl
 * @Description: 管理APP运营商认证接口调用
 * @author YabinLi
 * @date 2017年6月7日 下午3:30:49
 * @version 1.0
 */
@Service("qyjapiService")
public class IQyjapiServiceImpl implements IQyjapiService {
	private static final Logger LOGGER = LoggerFactory.getLogger(IQyjapiServiceImpl.class);
	@Value("${system.tenantId:}")
	private String tenant;
	@Value("${system.clientId:}")
	private String clientId;
	@Value("${qyjapi.appCertify:}")
	private String appCertify;
	@Value("${system.productId:}")
	private String productId;
	@Value("${spring.rabbitmq.qyjapiRoutingkey}")
	private String routingkey;
	@Autowired
	private LoanService loanService;
	@Autowired
	private RabbitProducer rabbitProducer;
	/**
	 * Title: triggerCertifyTask
	 * 
	 * @Description: 调用APP运营商接口，触发认证流程
	 * @param @param
	 *            appId
	 * @param @return
	 *            设定文件
	 * @author YabinLi
	 * @throws Exception
	 */
	@Override
	public boolean triggerCertifyTask(String appId, String requestType) throws Exception {
		boolean isSuccessAction = false;
		LOGGER.info("处理任务,进件编号:{} ", appId);
		Map<String, String> headers = getDefaultHeadMap();
		Map<String, Object> param = new HashMap<String, Object>();
		if (DictItem.QYJ_APP_CERTIFY.equals(requestType)) {
			param.put("qcCode", appId);
			param.put("requestType", requestType);
			param.put("requestJson", "");
			param.put("appKey", DictItem.QYJ_APP_CERTIFY_KEY);
		} else if (DictItem.QYJ_LOANAPPLY_REJECT.equals(requestType)) {
			param.put("qcCode", appId);
			param.put("requestType", requestType);
			param.put("requestJson", "");
			param.put("appKey", DictItem.QYJ_APP_CERTIFY_KEY);
		}
		param = this.getSigned(param);
		// 调用APP运营商认证接口
		LOGGER.info("调用App运营商接口{}任务, 接口地址url为:{}, 调用参数appId为:{}", requestType, appCertify, appId);
		String result = HttpClientUtil.doPostWithJson(appCertify, param, headers);
		LOGGER.info("处理{}任务, 返回结果:{}", appCertify ,result);
		if (StringUtils.isNotBlank(result)) {
			isSuccessAction = (boolean) JsonUtil.convert(result, Map.class).get("responseStatus");
		}
		return isSuccessAction;
	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	private String getHashByMd5(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(message.getBytes("UTF-8"));

			return this.bytesToHexString(digest);
		} catch (Exception ex) {
			return "";
		}
	}

	private Map<String, Object> getSigned(Map<String, Object> map) {
		String secret = "459D9FCA17E3A950DEAE755D13578292";
		StringBuilder sb = new StringBuilder(secret);
		TreeMap<String, Object> treeMap = new TreeMap<>(map);
		for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
			if (entry.getValue() == null || "".equals(entry.getValue())) {
				continue;
			}
			sb.append(entry.getKey());
			sb.append(entry.getValue().toString());
		}
		sb.append(secret);
		String Sign = this.getHashByMd5(sb.toString());
		map.put("sign", Sign);
		return map;
	}

	/**
	 * @return
	 * 
	 * 		设置bpms需要的请求头信息
	 */
	private Map<String, String> getDefaultHeadMap() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.put("Tenant", tenant);
		headers.put("clientId", clientId);
		return headers;
	}
	
	@Override
	public void sendLoanDetail(String appId) {
		try {
			if (StringUtil.isNotBlank(appId)) {
				Map<String, Object> map = loanService.queryLoanDetail(appId);
				MQBaseMessage message = new MQBaseMessage();
				message.setAppId(appId);
				message.setTenantId(tenant);
				message.setClientId(clientId);
				message.setProductId(productId);
				message.setTopicName(routingkey);
				
				List<Object> data = new ArrayList<Object>();
				data.add(map);
				message.setMessageObjects(data);
				rabbitProducer.sendMessage(message,"qyjapiRabbitTemplate");
			}
		} catch (Exception e) {
			LOGGER.error("查询进件详情接口失败", e);
		}
	}
}
