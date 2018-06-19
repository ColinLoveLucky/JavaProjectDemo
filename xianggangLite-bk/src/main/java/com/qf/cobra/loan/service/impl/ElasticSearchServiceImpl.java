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
* @Title: ElasticSearchServiceImpl.java 
* @Package com.qf.cobra.loan.service.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月11日 下午3:07:38 
* @version V1.5   
*/
package com.qf.cobra.loan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.qf.cobra.loan.service.IElasticSearchService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.util.HttpClientUtil;

/** 
 * @ClassName: ElasticSearchServiceImpl 
 * @Description: 调用Elasticsearch接口获取对应的历史进件手机号码和电话号码信息
 * @author YabinLi
 * @date 2017年7月11日 下午3:07:38 
 * @version 1.5 
 */
@Service
public class ElasticSearchServiceImpl implements IElasticSearchService{
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
	
	@Value("${elasticsearch.getMobileUrl}")
	private String getMobileUrl;
	@Value("${elasticsearch.getMobilePageSize}")
	private String getMobilePageSize;
	@Value("${elasticsearch.setConditionNumber}")
	private String setConditionNumber;
	@Autowired
	private MongoOperate mongoTemplate;
	
	@Override
	public List<Map<String, Object>> getMobileChecked(String appId){
		LOGGER.info("开启进件编号：{}，对应的手机号和电话号码的历史校验！", appId);
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		LoanApply loanApply = (LoanApply)mongoTemplate.findOne(queryParam, LoanApply.class);
		int size = Integer.parseInt(getMobilePageSize);
		int from = 0;
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		int total = requestElasticSearch(loanApply, returnList, size, from);
		while(total > (size + from)){
			from = from + size;
			requestElasticSearch(loanApply, returnList, size, from);
		}
		return returnList;
	}
	/**
	 * @Title: requestElasticSearch 
	 * @Description:  调用elasticsearch接口，解析当前分页之中的返回数据数据
	 * @param @param loanApply
	 * @param @param returnList
	 * @param @param size
	 * @param @param from
	 * @param @return    设定文件 
	 * @return int    返回类型 
	 * @date 2017年7月14日 下午1:46:30
	 * @author YabinLi
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private int requestElasticSearch(LoanApply loanApply, List<Map<String, Object>> returnList, int size, int from){
		Map<String, Set<String>> dataMap  = new HashMap<String, Set<String>>();
		String telephoneCollection[] = getTelephoneByLoanApp(loanApply.getLoanData());
		Map<String, Object> param = getParam(loanApply, telephoneCollection, size, from);
		Map<String, String> headers = getDefaultHeadMap();
		String url = getMobileUrl;
		int total = 0;
		try {
			String result = HttpClientUtil.doPostWithJson(url, param, headers);
			Map<String, Object> resultMap = JSONObject.parseObject(result, Map.class);
			total = (int) ((Map<String, Object>)resultMap.get("hits")).get("total");
			dataMap = resultAnalysis(loanApply.getAppId(), resultMap, telephoneCollection);
			for(Entry<String, Set<String>> temp : dataMap.entrySet()){
				boolean flag = true;
				for(int i = 0; i < returnList.size(); i++){
					Map<String, Object> returnListMap = returnList.get(i);
					if(returnListMap.get("telNum").toString().equals(temp.getKey())){
						flag = false;
						Set<String> applyIdSet = (Set<String>) returnListMap.get("applyIdList");
						applyIdSet.addAll(temp.getValue());
						returnListMap.put("applyIdList", applyIdSet);
					}
				}
				if(flag){
					Map<String, Object> returnMap = new HashMap<String, Object>();
					returnMap.put("telNum", temp.getKey());
					returnMap.put("applyIdList", temp.getValue());
					returnList.add(returnMap);
				}
			}
			LOGGER.info("结束进件编号：{}，对应的手机号和电话号码的历史校验！其对应的校验结果：{}", loanApply.getAppId(), returnList);
		} catch (Exception e) {
			LOGGER.error("结束进件编号：{}，对应的手机号和电话号码的历史校验！其对应的校验结果：{}", loanApply.getAppId(), e);
		}
		return total;
	}
	/**
	 * @Title: getDefaultHeadMap 
	 * @Description: 接口调用请求头设置
	 * @param @return    设定文件 
	 * @return Map<String,String>    返回类型 
	 * @date 2017年7月12日 上午11:02:57
	 * @author YabinLi
	 * @throws
	 */
	private Map<String, String> getDefaultHeadMap() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}
	/**
	 * @Title: getParam 
	 * @Description: 接口调用请求body设置
	 * @param @param loanApply
	 * @param @return    设定文件 
	 * @return Map<String,Object>    返回类型 
	 * @date 2017年7月12日 上午11:03:18
	 * @author YabinLi
	 * @throws
	 */
	private Map<String, Object> getParam(LoanApply loanApply, String telephoneCollection[], int size, int from){
		Map<String, Object> dataParam = new HashMap<String, Object>();
		Map<String, Object> queryData = new HashMap<String, Object>();
		Map<String, Object> boolData = new HashMap<String, Object>();
		Map<String, Object> mustData = new HashMap<String, Object>();
		List<String> sourceList = new ArrayList<String>();
		sourceList.add("appId");
		sourceList.add("data.personalInfo.mobilePhone");
		sourceList.add("data.personalInfo.telephone");
		sourceList.add("data.employmentInfo.tel");
		sourceList.add("data.contactInfo.phone");
		sourceList.add("data.contactInfo.telephone");
		//must条件
		mustTerms(mustData);
		//should 条件
		List<Object> shouldList = new ArrayList<Object>();
		shouldTerms(shouldList, telephoneCollection);
		boolData.put("must", mustData);
		boolData.put("should", shouldList);
		boolData.put("minimum_should_match", setConditionNumber);
		queryData.put("bool", boolData);
		dataParam.put("size", size);
		dataParam.put("from", from);
		dataParam.put("query", queryData);
		dataParam.put("_source", sourceList);
		return dataParam;
	}
	/**
	 * @Title: mustTerms 
	 * @Description: 查询条件之中的必要条件设置(and)
	 * @param @param mustData    设定文件 
	 * @return void    返回类型 
	 * @date 2017年7月12日 上午11:03:49
	 * @author YabinLi
	 * @throws
	 */
	private void mustTerms(Map<String, Object> mustData){
		Map<String, Object> dataKey = new HashMap<String, Object>();
		dataKey.put("dataKey", "cobra");
		mustData.put("term", dataKey);
	}
	/**
	 * @Title: shouldTerms 
	 * @Description: 查询条件之中的非必要条件设置 (or)
	 * @param @param shouldList
	 * @param @param telephoneCollection    设定文件 
	 * @return void    返回类型 
	 * @date 2017年7月12日 上午11:04:16
	 * @author YabinLi
	 * @throws
	 */
	private void shouldTerms(List<Object> shouldList, String telephoneCollection[]){
		//个人手机号
		Map<String, Object> persionMp = new HashMap<String, Object>();
		Map<String, Object> persionMpTerm = new HashMap<String, Object>();
		persionMp.put("data.personalInfo.mobilePhone", telephoneCollection);
		persionMpTerm.put("terms", persionMp);
		shouldList.add(persionMpTerm);
		//个人电话号
		Map<String, Object> persionTel = new HashMap<String, Object>();
		Map<String, Object> persionTelTerm = new HashMap<String, Object>();
		persionTel.put("data.personalInfo.telephone", telephoneCollection);
		persionTelTerm.put("terms", persionTel);
		shouldList.add(persionTelTerm);
		//个人公司电话
		Map<String, Object> employmentTelMap = new HashMap<String, Object>();
		Map<String, Object> employmentTelTermMap = new HashMap<String, Object>();
		employmentTelMap.put("data.employmentInfo.tel", telephoneCollection);
		employmentTelTermMap.put("terms", employmentTelMap);
		shouldList.add(employmentTelTermMap);
		//联系人
		//手机号
		Map<String, Object> contactInfoPhoneMap = new HashMap<String, Object>();
		Map<String, Object> contactInfoPhoneTermMap = new HashMap<String, Object>();
		contactInfoPhoneMap.put("data.contactInfo.phone", telephoneCollection);
		contactInfoPhoneTermMap.put("terms", contactInfoPhoneMap);
		shouldList.add(contactInfoPhoneTermMap);
		//电话号
		Map<String, Object> contactInfoTelMap = new HashMap<String, Object>();
		Map<String, Object> contactInfoTelTermMap = new HashMap<String, Object>();
		contactInfoTelMap.put("data.contactInfo.telephone", telephoneCollection);
		contactInfoTelTermMap.put("terms", contactInfoTelMap);
		shouldList.add(contactInfoTelTermMap);
	}
	/**
	 * @Title: getTelephoneByLoanApp 
	 * @Description: 获取当前进件之中的所有要进件校验的手机号和电话号
	 * @param @param loanDataMap
	 * @param @return    设定文件 
	 * @return String[]    返回类型 
	 * @date 2017年7月12日 上午11:05:28
	 * @author YabinLi
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private String[] getTelephoneByLoanApp(Map<String, Object> loanDataMap){
		Set<String> dataSet = new HashSet<String>();
		Map<String, Object> personalInfoMap = null;
		Map<String, Object> employmentInfoMap = null;
		List<Object> contactInfoList = null;
		if(loanDataMap.get("personalInfo") != null && loanDataMap.get("personalInfo") instanceof Map){
			personalInfoMap = (Map<String, Object>) loanDataMap.get("personalInfo");
		}
		if(loanDataMap.get("employmentInfo") != null && loanDataMap.get("employmentInfo") instanceof Map){
			employmentInfoMap = (Map<String, Object>) loanDataMap.get("employmentInfo");
		}
		if(loanDataMap.get("contactInfo") != null && loanDataMap.get("contactInfo") instanceof List){
			contactInfoList = (List<Object>) loanDataMap.get("contactInfo");
		}
		if(personalInfoMap != null){
			//个人手机号
			Object mobilePhone = personalInfoMap.get("mobilePhone");
			if(mobilePhone != null && StringUtils.isNotBlank(mobilePhone.toString())){
				dataSet.add(mobilePhone.toString());
			}
			//个人电话号
			Object telephone = personalInfoMap.get("telephone");
			if(telephone != null && StringUtils.isNotBlank(telephone.toString())){
				dataSet.add(telephone.toString());
			}
		}
		//个人公司电话
		if(employmentInfoMap != null){
			Object employmentTel = employmentInfoMap.get("tel");
			if(employmentTel != null && StringUtils.isNotBlank(employmentTel.toString())){
				dataSet.add(employmentTel.toString());
			}
		}
		//联系人
		//手机号&电话号
		if(contactInfoList != null){
			for(int i = 0; i < contactInfoList.size(); i++){
				if(contactInfoList.get(i) != null && contactInfoList.get(i) instanceof Map){
					Map<String, Object> contactInfoListMap = (Map<String, Object>) contactInfoList.get(i);
					Object contactInfoPhone = contactInfoListMap.get("phone");
					Object contactInfoTel = contactInfoListMap.get("telephone");
					if(contactInfoPhone != null && StringUtils.isNotBlank(contactInfoPhone.toString())){
						dataSet.add(contactInfoPhone.toString());
					}
					if(contactInfoTel != null && StringUtils.isNotBlank(contactInfoTel.toString())){
						dataSet.add(contactInfoTel.toString());
					}
				}
			}
		}
		String dataArray[] = new String[dataSet.size()];
		int count = 0;
		for(String temp : dataSet){
			dataArray[count] = temp;
			++count;
		}
		return dataArray;
	}
	/**
	 * @Title: resultAnalysis 
	 * @Description: 解析接口调用结果,获取对于的进件之中的手机号和电话号与历史进件编号映射关系
	 * @param @param resultMap
	 * @param @param telephoneCollection
	 * @param @return    设定文件 
	 * @return Map<String,String>    返回类型 
	 * @date 2017年7月12日 上午11:06:14
	 * @author YabinLi
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Set<String>> resultAnalysis(String applyIdPage, Map<String, Object> resultMap, String telephoneCollection[]){
		Map<String, Set<String>> dataMap = new HashMap<String, Set<String>>();
		//此处数据结构由elasticsearch接口返回决定
		List<Object> hitsList = ((List<Object>)((Map<String, Object>)resultMap.get("hits")).get("hits"));
		for(int i = 0; i < hitsList.size(); i++){
			//此处数据结构由elasticsearch接口返回决定，当hitsList长度非零时，_source字段一定存在
			Map<String, Object> sourceMap = (Map<String, Object>) ((Map<String, Object>)hitsList.get(i)).get("_source");
			String applyId = sourceMap.get("appId").toString();
			if(!applyIdPage.equals(applyId)){
				Map<String, Object> dataMapTemp = (Map<String, Object>) sourceMap.get("data");
				Map<String, Object> personalInfoMap = null;
				Map<String, Object> employmentInfoMap = null;
				List<Object> contactInfoList = null;
				if(dataMapTemp.get("personalInfo") != null && dataMapTemp.get("personalInfo") instanceof Map){
					personalInfoMap = (Map<String, Object>) dataMapTemp.get("personalInfo");
				}
				if(dataMapTemp.get("employmentInfo") != null && dataMapTemp.get("employmentInfo") instanceof Map){
					employmentInfoMap = (Map<String, Object>) dataMapTemp.get("employmentInfo");
				}
				if(dataMapTemp.get("contactInfo") != null && dataMapTemp.get("contactInfo") instanceof List){
					contactInfoList = (List<Object>) dataMapTemp.get("contactInfo");
				}
				
				Set<String> telephoneSet = new HashSet<String>();
				//个人手机号与电话号信息
				if(personalInfoMap != null){
					Object mobilePhone = personalInfoMap.get("mobilePhone");
					Object telephone = personalInfoMap.get("telephone");
					if(mobilePhone != null && StringUtils.isNotBlank(mobilePhone.toString())){
						telephoneSet.add(mobilePhone.toString());
					}
					if(telephone != null && StringUtils.isNotBlank(telephone.toString())){
						telephoneSet.add(telephone.toString());
					}
				}
				//个人公司电话信息
				if(employmentInfoMap != null){
					Object employmentTel = employmentInfoMap.get("tel");
					if(employmentTel != null && StringUtils.isNotBlank(employmentTel.toString())){
						telephoneSet.add(employmentTel.toString());
					}
				}
				//联系人
				//手机号&电话号
				if(contactInfoList != null){
					for(int j = 0; j < contactInfoList.size(); j++){
						if(contactInfoList.get(j) != null && contactInfoList.get(j) instanceof Map){
							Map<String, Object> contactInfoListTemp = (Map<String, Object>) contactInfoList.get(j);
							Object contactInfoPhone = contactInfoListTemp.get("phone");
							Object contactInfoTel = contactInfoListTemp.get("telephone");
							if(contactInfoPhone != null && StringUtils.isNotBlank(contactInfoPhone.toString())){
								telephoneSet.add(contactInfoPhone.toString());
							}
							if(contactInfoTel != null && StringUtils.isNotBlank(contactInfoTel.toString())){
								telephoneSet.add(contactInfoTel.toString());
							}
						}
					}
				}
				for(int k = 0; k < telephoneCollection.length; k++){
					if(telephoneSet.contains(telephoneCollection[k])){
						if(dataMap.get(telephoneCollection[k]) == null){
							Set<String> applyIdSet = new HashSet<String>();
							applyIdSet.add(applyId);
							dataMap.put(telephoneCollection[k], applyIdSet);
						}else{
							Set<String> applyIdSet = dataMap.get(telephoneCollection[k]);
							applyIdSet.add(applyId);
							dataMap.put(telephoneCollection[k],applyIdSet);
						}
					}
				}
			}
		}
		return dataMap;
	}
}
