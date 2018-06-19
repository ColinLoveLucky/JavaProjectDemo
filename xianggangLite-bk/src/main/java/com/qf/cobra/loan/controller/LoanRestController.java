package com.qf.cobra.loan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.impl.ReviceBorrowerLoanPush2NCobraImpl;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.SessionUtil;

@RestController
@RequestMapping("/rest/loan")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoanRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanRestController.class);

	@Autowired
	private LoanService loanService;
	@Autowired
	MongoTemplate mongoTemplate;

	// loanApp调用进件接口
	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	public ResponseData loanApply(@RequestBody String json, HttpServletRequest request) throws Exception {
		ResponseData responseData = ResponseUtil.defaultResponse();
		LOGGER.info("接收进件,进件信息为:{}", json);
		try {
			Thread.sleep(2000);
			json = json.replace("\\", "");
			json = json.substring(1, json.length() - 1);

			String clientId = request.getHeader("clientId");
			String tenantId = request.getHeader("tenantId");

			Map<String, Object> requestMap = JsonUtil.convert(json, Map.class);
			requestMap.remove("applyLoanId");
			requestMap.put("clientId", clientId);
			requestMap.put("tenantId", tenantId);
			Object loanDataObj = MapUtils.getObject(requestMap, "loanData");
			if (loanDataObj instanceof List) {
				List<Map<String, Object>> loanDataList = (List<Map<String, Object>>) loanDataObj;
				Map<String, Object> loanData = loanDataList.get(0);
				Map loanAppInfo = MapUtils.getMap(loanData, "loanAppInfo");
				String loanAppTime = MapUtils.getString(loanAppInfo, "loanAppTime");
				if (StringUtils.isBlank(loanAppTime)) {
					loanAppInfo.put("loanAppTime", MapUtils.getString(requestMap, "timestamp"));
					loanData.put("loanAppInfo", loanAppInfo);
				}
				requestMap.put("loanData", loanData);
				json = JsonUtil.convert(requestMap);
			} else {
				Map loanAppInfo = MapUtils.getMap((Map<String, Object>) loanDataObj, "loanAppInfo");
				String loanAppTime = MapUtils.getString(loanAppInfo, "loanAppTime");
				if (StringUtils.isBlank(loanAppTime)) {
					loanAppInfo.put("loanAppTime", MapUtils.getString(requestMap, "timestamp"));
				}
				json = JsonUtil.convert(requestMap);
			}
			loanService.receiveLoanApply(json);
		} catch (Exception e) {
			LOGGER.error("接收进件结果异常,进件信息为:{}", json, e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("借款信息接收失败");
		}
		return responseData;
	}

	@RequestMapping(value = "/start/{appId}", method = RequestMethod.POST)
	public ResponseData start(@PathVariable("appId") String appId) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		LOGGER.info("启动流程appId:{}", appId);
		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanInfo = mongoTemplate.findOne(query, LoanApply.class);
		try {
			loanService.startBorrowerProcess(loanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData;
	}

	@RequestMapping(value = "/loanApp/apply", method = RequestMethod.POST)
	public ResponseData loanAppApply(@RequestBody String json, HttpServletRequest request) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		LOGGER.info("接收进件,进件补全信息为:{}", json);
		try {
			json = json.replace("\\", "");
			json = json.substring(1, json.length() - 1);

			String clientId = request.getHeader("clientId");
			String tenantId = request.getHeader("tenantId");

			Map<String, Object> requestMap = JsonUtil.convert(json, Map.class);
			requestMap.remove("applyLoanId");
			requestMap.put("clientId", clientId);
			requestMap.put("tenantId", tenantId);
			Object loanDataObj = MapUtils.getObject(requestMap, "loanData");
			if (loanDataObj instanceof List) {
				List<Map<String, Object>> loanDataList = (List<Map<String, Object>>) loanDataObj;
				Map<String, Object> loanData = loanDataList.get(0);
				Map loanAppInfo = MapUtils.getMap(loanData, "loanAppInfo");
				String loanAppTime = MapUtils.getString(loanAppInfo, "loanAppTime");
				if (StringUtils.isBlank(loanAppTime)) {
					loanAppInfo.put("loanAppTime", MapUtils.getString(requestMap, "timestamp"));
					loanData.put("loanAppInfo", loanAppInfo);
				}
				requestMap.put("loanData", loanData);
				json = JsonUtil.convert(requestMap);
			} else {
				Map loanAppInfo = MapUtils.getMap((Map<String, Object>) loanDataObj, "loanAppInfo");
				String loanAppTime = MapUtils.getString(loanAppInfo, "loanAppTime");
				if (StringUtils.isBlank(loanAppTime)) {
					loanAppInfo.put("loanAppTime", MapUtils.getString(requestMap, "timestamp"));
				}
				json = JsonUtil.convert(requestMap);
			}
			loanService.receiveLoanApplyDetail(json);
		} catch (Exception e) {
			LOGGER.error("接收进件结果异常,进件信息为:{}", json, e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("借款信息接收失败");
		}
		
		return responseData;
	}

	@Autowired
	BeanFactory beanFactory;

	@RequestMapping(value = "/ncobra/audit/{appId}", method = RequestMethod.POST)
	public ResponseData ncobraAudit(@PathVariable("appId") String appId) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		LOGGER.info("loanApp信息同步ncobra appId:{}", appId);
		ReviceBorrowerLoanPush2NCobraImpl reviceTaskService = beanFactory.getBean("BorrowerLoanPush2NCobra",
				ReviceBorrowerLoanPush2NCobraImpl.class);
		reviceTaskService.excute(appId);
		return responseData;
	}

	/**
	 * loanApp运营商认证调用回调(手机运营商认证后调用)
	 * 
	 * @author JinbaoYang
	 * @param json
	 * @return
	 */
	/*
	 * @RequestMapping(value = "/completeMobileCert", method =
	 * RequestMethod.POST) public ResponseData getLoanAppCheck(@RequestBody
	 * String json,HttpServletRequest request) { ResponseData responseData =
	 * ResponseUtil.defaultResponse(); LOGGER.info("接收运营商认证结果,进件信息为:{}", json);
	 * try { Map<String, Object> requestMap = JsonUtil.convert(json, Map.class);
	 * Map<String, String> result = (Map<String, String>)
	 * requestMap.get("result"); CarrieroperatorResult carrieroperatorResult =
	 * new CarrieroperatorResult();
	 * carrieroperatorResult.setAppId(requestMap.get("extId").toString());
	 * carrieroperatorResult.setPolicyCode(requestMap.get("policyCode").toString
	 * ()); carrieroperatorResult.setServiceCode(requestMap.get("serviceCode").
	 * toString()); carrieroperatorResult.setStatus(result.get("stateCode"));
	 * carrieroperatorResult.setMessageDesc(result.get("stateDesc"));
	 * loanService.saveCarrieroperatorResult(carrieroperatorResult); } catch
	 * (Exception e) { LOGGER.info("接收运营商认证结果异常", e);
	 * responseData.setCode(ResponseCode.SYSTEM_ERROR);
	 * responseData.setMsg(e.getMessage()); } return responseData; }
	 */

}
