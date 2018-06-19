package com.qf.cobra.loan.controller;


import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.CarrieroperatorResult;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
@RequestMapping("/carrieroperator")
public class CarrieroperatorController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CarrieroperatorController.class);

	@Autowired
	private LoanService loanService;
	 
	
/*	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/carrieroperatorCert", method = RequestMethod.POST)
	public ResponseData<CarrieroperatorResult> carrieroperatorCert(@RequestBody Map<String, Object> requestBody) {
		ResponseData<CarrieroperatorResult> responseData = ResponseUtil.defaultResponse();
		try {
			
			String appId =MapUtils.getString(requestBody, "appId");
			CarrieroperatorResult carrieroperatorResult = loanService.getCarrieroperatorByAppId(appId);
			if(carrieroperatorResult ==null){
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg("未查找到进件编号："+appId+"的运营商数据状态信息");
				return responseData;
			}
			responseData.setData(carrieroperatorResult);
		} catch (Exception e) {
			LOGGER.info("获得运营商数据错误", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}*/
/*	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/carrieroperatorCert", method = RequestMethod.POST)
	public ResponseData<CarrieroperatorResult> carrieroperatorCert(@RequestBody Map<String, Object> requestBody) {
		ResponseData<CarrieroperatorResult> responseData = ResponseUtil.defaultResponse();
		try {
		String appId =MapUtils.getString(requestBody, "appId");
		CarrieroperatorResult carrieroperatorResult = null;
			do{
				 carrieroperatorResult = loanService.getCarrieroperatorByAppId(appId);
				  Thread.currentThread().sleep(1000);
			}while(carrieroperatorResult == null);
			if(carrieroperatorResult ==null){
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg("未查找到进件编号："+appId+"的运营商数据状态信息");
				return responseData;
			}
			responseData.setData(carrieroperatorResult);
		} catch (Exception e) {
			LOGGER.info("获得运营商数据错误", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
		
	}*/
}
