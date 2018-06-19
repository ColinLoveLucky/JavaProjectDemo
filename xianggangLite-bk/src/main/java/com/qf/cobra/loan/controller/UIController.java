package com.qf.cobra.loan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.qf.cobra.exception.TokenException;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loanapp.service.ILoanAppService;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
public class UIController {
	@Autowired
	private LoanService loanService;

	@Value("${system.tenantId:}")
	private String tenantId;
	@Value("${system.clientId:}")
	private String clientId;
	@Autowired
	private ILoanAppService loanAppService;

	/**
	 * @param request
	 * @param header
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/ui/loanApply", method = RequestMethod.POST)
	public ResponseData loanApply(@RequestBody String json) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		// 去除左右空格
//		json = json.replaceAll("(\"\\s*)|(\\s*\")", "\"");
		// 去除特殊字符
//		json = json.replaceAll("\t|\r|\n", "");
		
		try {
			JSONObject loanApply = JSONObject.parseObject(json);

			String timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
			JSONObject jsonObject = JSONObject.parseObject(json);
			jsonObject.put("applyLoanId", appIdCreate());
			jsonObject.put("timestamp", timestamp);
			jsonObject.put("tenantId", tenantId);
			jsonObject.put("clientId", clientId);
			jsonObject.getJSONObject("loanData")
					.getJSONObject("loanAppInfo")
					.put("loanAppTime", timestamp);
			Map<String, Object> extras = new HashMap<String, Object>();
			extras.put("version", "0");
			jsonObject.getJSONObject("loanData").put("extras", extras);
			// json过滤
			SimplePropertyPreFilter propertyFilter = new SimplePropertyPreFilter(
					"applyLoanId","timestamp","loanData",
					"loanAppInfo","appAmount","acceptAmount","productCode","appCity","loanMaturity",
					"personalInfo","name","idCard","maritalStatus","mobilePhone","maritalStatusOther",
					"contactInfo","relation","name","phone","relativesName",
					"extras","version");
			String newJson = JSONObject
					.toJSONString(jsonObject, propertyFilter);

			Map<String, Object> request = JsonUtil.convert(newJson, Map.class);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add(MapUtils.getMap(request, "loanData"));
			request.put("loanData", list);

			Map<String, Object> map = loanAppService.sendLoanApply(request);
			String code = MapUtils.getString(map, "responseCode", "");
			String message = MapUtils.getString(map, "responseMessage", "");

			if (!StringUtils.equals(code, "2000")) {
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg(message);
			} else {
				String appId = MapUtils.getString(map, "responseBody");
				jsonObject.remove("applyLoanId");
				jsonObject.put("appId", appId);
				loanService.addLoanApply(jsonObject);
			}
		} catch (TokenException e) {
			responseData.setCode(ResponseCode.TOKEN_INVALID);
			responseData.setMsg(e.getMessage());
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("添加申请失败");
		}
		return responseData;
	}
	
	private String appIdCreate() {
		return new StringBuffer()
				.append(DateTime.now().toString("yyyyMMddHHmmss"))
				.append(String.valueOf(RandomUtils.nextLong(10000, 99999)))
				.toString();
	}
}
