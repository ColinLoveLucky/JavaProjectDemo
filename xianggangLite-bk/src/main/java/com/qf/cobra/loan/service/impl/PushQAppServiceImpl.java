package com.qf.cobra.loan.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.qf.cobra.exception.RespondFailedException;
import com.qf.cobra.exception.UnexpectedResponseException;
import com.qf.cobra.loan.service.IPushQAppService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.pojo.PushRecord;
import com.qf.cobra.qapp.QappProperties;
import com.qf.cobra.qapp.service.IQappService;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.HttpClientUtil;
import com.qf.cobra.util.JsonUtil;
@Service
@SuppressWarnings("unchecked")
public class PushQAppServiceImpl implements IPushQAppService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushQAppServiceImpl.class);
//	@Autowired
//	private RestTemplate restTemplate;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired 
	private IQappService qappService;
	@Autowired
	private QappProperties properties;

	
	@Override
	public void pushQApp(String appId) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		
		Map<String, Object> map = match(loanApply);
		
		PushRecord record = new PushRecord();
		record.setAppId(loanApply.getAppId());
		record.setTimestamp(DateUtil.formatCurrentDateTime());
		record.setPushData(map);
		
		Boolean isSuccess = false;
		if(MapUtils.isEmpty(map)){
			LOGGER.error("推送进件信息数据转化出现异常,进件编号:{}",appId);
			record.setPushResult("异常-数据转化");
			mongoTemplate.save(record);
			throw new UnexpectedResponseException(String.format("推送借款信息数据转化出现异常,借款编号:{}",appId));
		}
		
		String result = null;
		try {
			LOGGER.info("开始推送进件信息,进件编号:{}", appId);
			result = HttpClientUtil.doPostWithJson(properties.getPushUrl(),
					map, headers);
			result = result == null ? "" : result;
			// {"ErrCode":"1001","ErrMsg":null,"BizErrorMsg":"更新条目时出错。有关详细信息，请参见内部异常。","IsBizSuccess":false}
			LOGGER.info("结束推送进件信息,进件编号:{},请求结果为:{}", appId, result);
		} catch (Exception e) {
			LOGGER.error("推送进件信息出现异常,进件编号:{}", appId, e);
			record.setPushResult("异常-请求-" + e.getMessage());
			mongoTemplate.save(record);
			throw new RespondFailedException(e);
		}

		Map<String, Object> resultMap = JsonUtil.convert(result, Map.class);
		isSuccess = MapUtils.getBoolean(resultMap, "IsBizSuccess", false);
		record.setPushResult(result);
		mongoTemplate.save(record);

		if (!isSuccess) {
			throw new UnexpectedResponseException(String.format(
					"推送进件信息失败,进件编号:{}", appId));
		}
	}
	
	public Map<String, Object> match(LoanApply loanApply) {
		Map<String, Object> loanAppInfo = MapUtils.getMap(loanApply.getLoanData(), "loanAppInfo");
		String productCode = MapUtils.getString(loanAppInfo, "productCode");
		// 获取产品相关参数
		Map<String,Object> productInterest = qappService.queryProductInterest(productCode);
		
		Map<String,Object> tmpMap = new HashMap<String, Object>();
		tmpMap.put("annualRate",MapUtils.getString(MapUtils.getMap(productInterest, "pInterest"), "rate", ""));//年利率
//		tmpMap.put("marginRate", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "marginRate", ""));//保证金费率
		//tmpMap.put("eachLoanFee", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "eachLoanFee", ""));//每期借款账户管理费率
		tmpMap.put("reservesRatio", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "reservesRatio", ""));//逾期风险补偿金率
		tmpMap.put("consultationChargeRatio", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "consultationChargeRatio", ""));//借款咨询费（含风补）
        //tmpMap.put("afterLoanFee", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "afterLoanFee", ""));//贷款管理费
		tmpMap.put("splitFees", MapUtils.getObject(productInterest, "splitFees"));//贷款管理费
		tmpMap.put("oldSplitFees", MapUtils.getObject(productInterest, "oldSplitFees"));//贷款管理费
		tmpMap.put("guaranteeRate", MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "guaranteeRate", ""));//担保费

		Map<String,Object> result = null;
		if(tmpMap.containsValue("") || tmpMap.containsValue("0")){
			LOGGER.error("产品编号:{},查询产品子类明细-存在未配置的费率",productCode);
		}else{
			result = new HashMap<String, Object>();
			result.put("json", matchAppMain(loanApply,productInterest,tmpMap));
		}
		return result;
	}
	
	public Map<String, Object> matchAppMain(LoanApply loanApply,Map<String,Object> productInterest,Map<String,Object> rateMap){
		Map<String, Object> appMain = new HashMap<String, Object>();
		
		List<Map<String,Object>> appLoan = new ArrayList<Map<String,Object>>();
		appLoan.add(matchAppLoan(loanApply,productInterest,rateMap));
		appMain.put("APP_LOAN", appLoan);
		appMain.put("APP_FEE_SPLITTING", rateMap.get("splitFees"));
		
		List<Map<String,Object>> appContact = matchAppContact(loanApply);
		appMain.put("APP_CONTACT", appContact);
		
		
		List<Map<String,Object>> appCustomer = new ArrayList<Map<String,Object>>();
		appCustomer.add(matchAppCustomer(loanApply));
		appMain.put("APP_CUSTOMER", appCustomer);
		
		
		List<Map<String,Object>> appJob = new ArrayList<Map<String,Object>>();
		appJob.add(matchAppJob(loanApply));
		appMain.put("APP_JOB", appJob);
		
		List<Map<String,Object>> appBankcard = new ArrayList<Map<String,Object>>();
		appBankcard.add(matchAppBankcard(loanApply));
		appMain.put("APP_BANKCARD", appBankcard);
		
		List<Map<String,Object>> appStaffOnly = new ArrayList<Map<String,Object>>();
		appStaffOnly.add(matchAppStaffOnly(loanApply));
		appMain.put("APP_STAFF_ONLY", appStaffOnly);
		
		Map<String, Object> loanAppInfo = MapUtils.getMap(loanApply.getLoanData(), "loanAppInfo");
//		Map<String, Object> appCity = MapUtils.getMap(loanAppInfo, "appCity");
		
		appMain.put("APP_CODE",loanApply.getAppId());
		appMain.put("LOGO",MapUtils.getString(MapUtils.getMap(productInterest, "pLogo"), "logo"));
		appMain.put("PRODUCT_CODE",MapUtils.getString(loanAppInfo, "productCode"));
		appMain.put("PRODUCT_NAME",MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "productName"));
//		appMain.put("APP_STATUS","");
//		appMain.put("CREATED_USER","");
		appMain.put("CREATED_TIME",loanApply.getTimestamp());
//		appMain.put("CHANGED_USER","");
//		appMain.put("CHANGED_TIME","");
		appMain.put("APPLY_CITY_CODE",MapUtils.getString(loanAppInfo, "appCity"));
//		appMain.put("APPLY_AREA_CODE","");
//		appMain.put("APPLY_PROV_CODE","");
		appMain.put("CUSTOMERTYPE",MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "fit4customerType"));
		appMain.put("PROD_VERSION",MapUtils.getString(MapUtils.getMap(productInterest, "pProduct"), "prodVersion"));
		appMain.put("SUBMIT_TIME",loanApply.getTimestamp());
		appMain.put("CHANGE_PRODUCT_FLAG","N");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("APP_MAIN", appMain);
		return map ;
	}

	public Map<String, Object> matchAppStaffOnly(LoanApply loanApply){
		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> salesInfo = MapUtils.getMap(loanData, "salesInfo");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CHANNEL_CODE",MapUtils.getString(salesInfo, "channelCode"));
		map.put("CHANNEL_NAME","");
		map.put("SALES_NAME",MapUtils.getString(salesInfo, "customerManagerName"));
		map.put("SALES_CODE",MapUtils.getString(salesInfo, "customerManagerCode"));
		map.put("CSAD_CODE",MapUtils.getString(salesInfo, "customerServiceCode"));
		map.put("CSAD_NAME",MapUtils.getString(salesInfo, "customerServiceName"));
//		map.put("MEMO","");      
//		map.put("MOTO_NAME","");
//		map.put("MOTO_NO","");
//		map.put("SEC_DEALER_PROV","");
//		map.put("SEC_DEALER_CITY","");
//		map.put("SEC_DEALER_NAME","");
//		map.put("DEPARTMENT_CODE","");
//		map.put("DEPARTMENT_NAME","");
//		map.put("DIVISION_CODE","");
//		map.put("DIVISION_NAME","");
		return map;
	}
	
	public Map<String, Object> matchAppBankcard(LoanApply loanApply){
		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> bankInfo = MapUtils.getMap(loanData, "bankInfo");
		List<String> branchCity = (List<String>) MapUtils.getObject(bankInfo, "branchCity");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("BANK_PROVINCE",CollectionUtils.isEmpty(branchCity)?"":branchCity.get(0));
		map.put("BANK_CITY",CollectionUtils.isEmpty(branchCity)?"":branchCity.get(1));
		map.put("BANK_SUB",MapUtils.getString(bankInfo, "branchDetails"));
		map.put("BANK_CODE",MapUtils.getString(bankInfo, "bankId"));
		map.put("BANK_NAME",MapUtils.getString(bankInfo, "bankName"));
		map.put("BANK_MOBILE",MapUtils.getString(bankInfo, "mobile"));
		map.put("BANK_ACCOUNT",MapUtils.getString(bankInfo, "accountNum"));
		return map;
	}
	
	public Map<String, Object> matchAppJob(LoanApply loanApply){
		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> employmentInfo = MapUtils.getMap(loanData, "employmentInfo");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COM_NAME",MapUtils.getString(employmentInfo, "companyName"));
		map.put("COM_PROPERTY",MapUtils.getString(employmentInfo, "companyType"));
		map.put("COM_PROPERTY_OTHER",MapUtils.getString(employmentInfo, "companyTypeOther"));
//		map.put("COM_TYPE","");
//		map.put("COM_TYPE_OTHER","");
		map.put("EMAIL",MapUtils.getString(employmentInfo, "commEmail"));
		map.put("COM_TEL_NO",MapUtils.getString(employmentInfo, "tel"));
		map.put("DATE_JOIN",MapUtils.getString(employmentInfo, "hiredate"));
		List<String> companyAddressCity = (List<String>) MapUtils.getObject(employmentInfo, "companyAddressCity");
		map.put("COM_PROVICE",CollectionUtils.isEmpty(companyAddressCity)?"":companyAddressCity.get(0));
		map.put("COM_CITY",CollectionUtils.isEmpty(companyAddressCity)?"":companyAddressCity.get(1));
		map.put("COM_ADDRESS",MapUtils.getObject(employmentInfo, "companyAddressDetails"));
		map.put("COM_INDUSTRY",MapUtils.getString(employmentInfo, "COM_INDUSTRY"));
		
//		map.put("EMP_ATTR","");
//		map.put("DISPATCH_COMPANY","");
//		map.put("STATION2_COM","");
//		map.put("STATION2_COM_OTHER","");
//		map.put("SHARE_RATIO","");
//		map.put("DATE_COM_REGISTER","");
//		map.put("COM_REGISTER_CAPITAL_AMT","");
//		map.put("COM_PERSONS","");
//		map.put("ISBUSINESS_LICENSE","");
		map.put("COM_POSITION",MapUtils.getString(employmentInfo, "position"));
		map.put("COM_POSITION_OTHER",MapUtils.getString(employmentInfo, "positionOther"));
//		map.put("JOB_TITLE",MapUtils.getString(employmentInfo, "duty"));
		//map.put("INDUSTRY",MapUtils.getString(employmentInfo, "industry"));
		map.put("DUTIES",MapUtils.getString(employmentInfo, "duty"));
		map.put("COM_DEPT",MapUtils.getString(employmentInfo, "dept"));
		return map;
	}
	
	public Map<String, Object> matchAppCustomer(LoanApply loanApply){
		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> personalInfo = MapUtils.getMap(loanData, "personalInfo");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("NAME",MapUtils.getString(personalInfo, "name"));
		map.put("USED_NAME",MapUtils.getString(personalInfo, "nameUsedBefore"));
		map.put("GENDER",MapUtils.getString(personalInfo, "sex"));
		map.put("AGE",MapUtils.getIntValue(personalInfo, "age"));
		map.put("ID_NO",MapUtils.getString(personalInfo, "idCard"));
		map.put("ID_TYPE","Id1");
		map.put("EDUCATION",MapUtils.getString(personalInfo, "educateLevel"));
		map.put("MARRAGE",MapUtils.getString(personalInfo, "maritalStatus"));
		map.put("MARRAGE_OTHER",MapUtils.getString(personalInfo, "maritalStatusOther"));
		map.put("HAS_LOCAL_HOUSE",MapUtils.getString(personalInfo, "houseStatus"));
//		map.put("RESIDENT_STATUS","");
//		map.put("RESIDENTOTHERS","");
		List<String> residenceAddress = (List<String>) MapUtils.getObject(personalInfo, "residenceAddressCity");
		map.put("RESIDENT_PROVINCE",CollectionUtils.isEmpty(residenceAddress)?"":residenceAddress.get(0));
		map.put("RESIDENT_CITY",CollectionUtils.isEmpty(residenceAddress)?"":residenceAddress.get(1));
		map.put("RESIDENT_ADDRESS",MapUtils.getString(personalInfo, "residenceAddressDetails"));
		List<String> permanentAddressCity = (List<String>) MapUtils.getObject(personalInfo, "permanentAddressCity");
		map.put("REGISTER_PROVINCE",CollectionUtils.isEmpty(permanentAddressCity)?"":permanentAddressCity.get(0));
		map.put("REGISTER_CITY",CollectionUtils.isEmpty(permanentAddressCity)?"":permanentAddressCity.get(1));
		map.put("REGISTER_ADDRESS",MapUtils.getString(personalInfo, "permanentAddressDetails"));
//		map.put("POSTCODE","");
//		map.put("YEARS_IN_LOCAL","");
//		map.put("RESIDENT_TEL_NO","");
//		map.put("RELATIONSHIP_OF_RESIDENT_TEL","");
		map.put("MOBILE1",MapUtils.getString(personalInfo, "mobilePhone"));
//		map.put("MOBILE2","");
//		map.put("MAX_CREDITLIMIT_OF_CCC","");
//		map.put("NUMS_OF_CCC","");
		map.put("EMAIL",MapUtils.getString(personalInfo, "email"));
		map.put("QQ",MapUtils.getString(personalInfo, "QQ"));
//		map.put("INCOME_ANNUAL","");
//		map.put("INCOME_MONTHLY_FROM_JOB","");
//		map.put("INCOME_MONTHLY_FROM_OTHER","");
//		map.put("MEMO_OF_INCOME_FROM_OTHER","");
		map.put("NUMS_OF_PROVIDE",MapUtils.getIntValue(personalInfo, "supportNumber"));
		map.put("WECHAT",MapUtils.getString(personalInfo, "WeChat"));
		map.put("CHILDREN_NUM",MapUtils.getIntValue(personalInfo, "childernNumber"));
		map.put("ISS_BANK_OF_CCC",MapUtils.getString(personalInfo, "creditBank"));
		map.put("USED_MONTH_OF_CCC",MapUtils.getIntValue(personalInfo, "usedMonth"));
		map.put("HAS_PAY_LATEFEE",MapUtils.getString(personalInfo, "islatePayment"));
		Map<String, Object> employmentInfo = MapUtils.getMap(loanData, "employmentInfo");
		map.put("INCOME_MONTHLY_FROM_JOB",MapUtils.getNumber(employmentInfo, "monthlyIncome"));
		map.put("INCOME_MONTHLY_TYPE",MapUtils.getString(employmentInfo, "monthlyPaymentMethod"));          
		map.put("INCOME_PAYDAY",MapUtils.getIntValue(employmentInfo, "monthlyPayDay"));
		map.put("LIABILITIES",MapUtils.getDoubleValue(employmentInfo, "LIABILITIES"));
		

		return map;
	}
	
	public List<Map<String, Object>> matchAppContact(LoanApply loanApply){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> loanData = loanApply.getLoanData();
		List<Map<String, Object>> contactInfo = new ArrayList<Map<String, Object>>();
		contactInfo = (List<Map<String, Object>>) MapUtils.getObject(loanData, "contactInfo",contactInfo);

		
		for (Map<String, Object> contactInfoMap : contactInfo) {
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("RELATIONSHIP",dictMapping(MapUtils.getString(contactInfoMap, "relation"),DICT_RELATIONSHIP));
			map.put("RELATIONSHIP",MapUtils.getString(contactInfoMap, "relation"));
			map.put("RELATIONSHIP_MEMO",MapUtils.getString(contactInfoMap, "relationOther"));
			map.put("NAME",MapUtils.getString(contactInfoMap, "name"));
			map.put("MOBILE",MapUtils.getString(contactInfoMap, "phone"));
			map.put("CONTACT_PROPERTY",MapUtils.getString(contactInfoMap, "contactProperty"));
			map.put("COM_NAME",MapUtils.getString(contactInfoMap, "jobTitle"));
			map.put("COM_DEPT",MapUtils.getString(contactInfoMap, "dept"));
			map.put("COM_POSITION",MapUtils.getString(contactInfoMap, "job"));
			List<String> companyAddressCity = (List<String>) MapUtils.getObject(contactInfoMap, "companyAddressCity");
			map.put("COM_PROVICE",CollectionUtils.isEmpty(companyAddressCity)?"":companyAddressCity.get(0));
			map.put("COM_CITY",CollectionUtils.isEmpty(companyAddressCity)?"":companyAddressCity.get(1));
			map.put("COM_ADDRESS",MapUtils.getString(contactInfoMap, "companyAddressDetails"));
			List<String> familyAddressCity = (List<String>) MapUtils.getObject(contactInfoMap, "familyAddressCity");
			map.put("RESIDENT_PROVINCE",CollectionUtils.isEmpty(familyAddressCity)?"":familyAddressCity.get(0));
			map.put("RESIDENT_CITY",CollectionUtils.isEmpty(familyAddressCity)?"":familyAddressCity.get(1));
			map.put("RESIDENT_ADDRESS",MapUtils.getString(contactInfoMap, "familyAddressDetails"));
			map.put("TELEPHONE",MapUtils.getString(contactInfoMap, "telephone"));
			map.put("FAMILY_KNOWING",MapUtils.getString(contactInfoMap, "isKown"));
			list.add(map);
		}
		return list;
	}

	public Map<String, Object> matchAppLoan(LoanApply loanApply,Map<String,Object> productInterest,Map<String,Object> rateMap){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> loanAppInfo = MapUtils.getMap(loanData, "loanAppInfo");
		
		BigDecimal appAmount = new BigDecimal(MapUtils.getString(loanAppInfo, "appAmount"));
		BigDecimal loanMaturity = new BigDecimal(MapUtils.getString(loanAppInfo, "loanMaturity"));
		Map<String, Object> calProductInterest = calProductInterest(appAmount,loanMaturity,rateMap);
		
		Map<String, Object> pInterest = MapUtils.getMap(productInterest, "pInterest");
		Map<String, Object> pProduct = MapUtils.getMap(productInterest, "pProduct");
		List<Map<String, Object>> crDataDic = (List<Map<String, Object>>) MapUtils.getObject(productInterest, "crDataDic");
		List<Map<String, Object>> dataType = (List<Map<String, Object>>) MapUtils.getObject(crDataDic.get(0), "dataType");
		String payType = MapUtils.getString(dataType.get(0), "dataCode");
		
		
		map.put("LOAN_PURPOSE",MapUtils.getString(loanAppInfo, "purpose"));
		map.put("MEMO_OF_LOAN_PURPOSE_OTHER" ,MapUtils.getString(loanAppInfo, "purposeOther"));
		map.put("APPLY_AMT" ,appAmount);
		map.put("TERMS" ,loanMaturity);
//		map.put("LOAN_AMT" ,MapUtils.getDouble(calProductInterest,"handAmount"));
//		map.put("LOAN_AMT" ,appAmount);
		map.put("LOAN_AMT" ,MapUtils.getDouble(calProductInterest,"contractAmount"));
		map.put("LOAN_AMT_OF_CONTRACT" ,MapUtils.getDouble(calProductInterest,"contractAmount"));
		map.put("PAY_AMT_MONTHLY_ACCEPTABLE" ,MapUtils.getDouble(loanAppInfo, "acceptAmount"));
//		map.put("PAY_AMT_FIRST_MONTHLY" ,"");
//		map.put("PAY_AMT_LAST_MONTHLY" ,"");
//		map.put("PAY_AMT_MONTHLY" ,"");
		map.put("DEFAULT_INTEREST_RATIO" ,MapUtils.getDouble(pInterest, "defaultInterestRatio"));
		map.put("LATE_FEE_RATIO" ,MapUtils.getDouble(pInterest, "lateFeeRatio"));
		map.put("SERVICE_CHARGE_RATIO" ,MapUtils.getDouble(pInterest, "serviceChargeRatio"));
		map.put("CONSULTATION_CHARGE_RATIO" ,MapUtils.getDouble(pProduct, "consultationChargeRatio"));
		map.put("RATE_TYPE" ,MapUtils.getString(pInterest, "rateType"));
		map.put("RATE" ,MapUtils.getDouble(pInterest, "rate"));
		map.put("PAYTYPE" ,payType);
		map.put("RESERVES_RATIO" ,MapUtils.getDouble(pProduct, "reservesRatio"));
		map.put("SERVICE_CHARGE_AMT" ,0);
		map.put("CONSULTATION_CHARGE_AMT" ,MapUtils.getDouble(calProductInterest, "consultationChargeAmount"));
		map.put("RESERVES_AMT" ,MapUtils.getDouble(calProductInterest, "reservesAmount"));
		map.put("GUARANTEE_RATE" ,MapUtils.getDouble(pProduct, "guaranteeRate"));
		map.put("GUARANTEE_FEE" ,MapUtils.getDouble(calProductInterest, "guaranteeAmount"));
		//map.put("ACCOUNT_MANAGEMENT_RATE" ,MapUtils.getDouble(pProduct, "eachLoanFee"));
		//map.put("ACCOUNT_MANAGEMENT_FEE" ,MapUtils.getDouble(calProductInterest, "eachLoanReeAmout"));
//		map.put("MARGIN_RATE" ,MapUtils.getDouble(pProduct, "marginRate"));
        //map.put("MARGIN_FEE" ,MapUtils.getDouble(calProductInterest, "marginAmount"));
		//BigDecimal afterLoanFee = new BigDecimal(MapUtils.getString(pProduct, "afterLoanFee", "0.0"));
		//if(BigDecimal.ZERO.compareTo(afterLoanFee)<0){
		//	map.put("POSTLOAN_MANAGEMENT_FEE" ,MapUtils.getDouble(calProductInterest,"afterLoanFeeAmount"));
		//}
		
		return map;
	}
	/**
	 * 计算费率
	 * @param productCode
	 * @param appAmount
	 * @param loanMaturity
	 * @return
	 */
	public Map<String, Object> calProductInterest(
			BigDecimal appAmount, BigDecimal loanMaturity,Map<String, Object> rateMap) {
		Map<String, Object> map = null;
		
		if(rateMap != null && !rateMap.isEmpty()){
			//年利率
			BigDecimal annualRate = new BigDecimal(MapUtils.getString(rateMap, "annualRate"));
			//保证金率
//			BigDecimal marginRate = new BigDecimal(MapUtils.getString(rateMap, "marginRate"));
			//每期借款账户管理费率
			//BigDecimal eachLoanFee = new BigDecimal(MapUtils.getString(rateMap, "eachLoanFee"));
			//逾期风险补偿费率
			BigDecimal reservesRatio = new BigDecimal(MapUtils.getString(rateMap, "reservesRatio"));
			//担保费
			BigDecimal guaranteeRate = new BigDecimal(MapUtils.getString(rateMap, "guaranteeRate"));
			//借款咨询费率
			BigDecimal consultationChargeRatio = new BigDecimal(MapUtils.getString(rateMap, "consultationChargeRatio"));
			//贷后管理费
			//BigDecimal afterLoanFee = new BigDecimal(MapUtils.getString(rateMap, "afterLoanFee"));

			BigDecimal splitFeeRate = BigDecimal.ZERO;
			BigDecimal splitFeeAmt = BigDecimal.ZERO;
			//分润需求，新增
			if(rateMap.containsKey("splitFees")){
				List<Map<String, Object>> splitFees = (List<Map<String, Object>>)rateMap.get("splitFees");
				if(splitFees != null && splitFees.size() > 0){
					for(Map<String, Object> splitFee:splitFees){
						if(splitFee.get("SPLITTING_RATIO")!=null){
							splitFeeRate = splitFeeRate.add(new BigDecimal(splitFee.get("SPLITTING_RATIO").toString()));
						}else if(splitFee.get("SPLITTING_FEE")!=null) {
							splitFeeAmt = splitFeeAmt.add(new BigDecimal(splitFee.get("SPLITTING_FEE").toString()));
						}
					}
				}
			}
			
			BigDecimal totalPremiumRate = BigDecimal.ZERO.add(consultationChargeRatio);
			BigDecimal contractAmount = appAmount.divide(BigDecimal.ONE.subtract(totalPremiumRate),0,RoundingMode.HALF_UP);
			//BigDecimal eachLoanReeAmout = contractAmount.multiply(eachLoanFee).setScale(2, RoundingMode.HALF_UP);
//			BigDecimal marginAmount = contractAmount.multiply(marginRate).setScale(2, RoundingMode.HALF_UP);
			//BigDecimal marginAmount = eachLoanReeAmout.multiply(loanMaturity).setScale(2, RoundingMode.HALF_UP);
			BigDecimal reservesAmount = contractAmount.multiply(reservesRatio).setScale(2, RoundingMode.HALF_UP);
//			BigDecimal splitFeeAmount = contractAmount.multiply(splitFeeRate).setScale(2, RoundingMode.HALF_UP);
			BigDecimal guaranteeAmount = contractAmount.multiply(guaranteeRate).setScale(2, RoundingMode.HALF_UP);
			//BigDecimal afterLoanFeeAmount = contractAmount.multiply(afterLoanFee).setScale(2, RoundingMode.HALF_UP);
			BigDecimal consultationChargeAmount = contractAmount.subtract(appAmount).subtract(guaranteeAmount).subtract(reservesAmount);
			
			BigDecimal payablePrincipalAmount = contractAmount.divide(loanMaturity, 2, RoundingMode.HALF_UP);
			BigDecimal payableInterestAmount = contractAmount.multiply(annualRate).multiply(new BigDecimal(7)).divide(new BigDecimal(360), 2, RoundingMode.HALF_UP);
			BigDecimal payableAmount =  BigDecimal.ZERO.add(payablePrincipalAmount).add(payableInterestAmount);
		
			//BigDecimal handAmount = appAmount.subtract(afterLoanFeeAmount).subtract(marginAmount).subtract(splitFeeAmt);
			
			map = new HashMap<String, Object>();
			//前置总费率
			map.put("totalPremiumRate", totalPremiumRate);
			//合同金额
			map.put("contractAmount", contractAmount);
			//保证金
//			map.put("marginAmount", marginAmount);
			//每期借款账户管理费
//			map.put("eachLoanReeAmout", eachLoanReeAmout);
			
			//逾期风险补偿金
			map.put("reservesAmount", reservesAmount);
			//担保费
			map.put("guaranteeAmount", guaranteeAmount);
			
			//每周应还本金
			map.put("payablePrincipalAmount", payablePrincipalAmount);
			//每周应还利息
			map.put("payableInterestAmount", payableInterestAmount);
			//每周还款额
			map.put("payableAmount", payableAmount);
			//借款咨询费
			map.put("consultationChargeAmount", consultationChargeAmount);
			//贷后管理费
//			map.put("afterLoanFeeAmount", afterLoanFeeAmount);
			
			//计算分润金额
			if(splitFeeRate.compareTo(BigDecimal.ZERO)>0){
				BigDecimal totalSplitAmt = BigDecimal.ZERO;
				List<Map<String, Object>> splitFees = (List<Map<String, Object>>)rateMap.get("splitFees");
				
				for(int i=0;i<splitFees.size();i++){
					Map<String, Object> splitFee = splitFees.get(i);
					if(splitFee.get("SPLITTING_RATIO")!=null){
						BigDecimal splitFeeRAmt = contractAmount.multiply(new BigDecimal(splitFee.get("SPLITTING_RATIO").toString())).setScale(2, RoundingMode.HALF_UP);
						totalSplitAmt = totalSplitAmt.add(splitFeeRAmt);
						splitFee.put("SPLITTING_FEE", splitFeeRAmt);
						
						if("10021".equals(splitFee.get("FEE_CODE").toString())){
							totalSplitAmt = totalSplitAmt.subtract(splitFeeRAmt);
							splitFee.put("SPLITTING_FEE", consultationChargeAmount);
						}
					}
				}
				
				if(rateMap.containsKey("oldSplitFees")){
					List<Map<String, Object>> oldSplitFees = (List<Map<String, Object>>)rateMap.get("oldSplitFees");
					
					for(int i=0;i<oldSplitFees.size();i++){
						Map<String, Object> splitFee = oldSplitFees.get(i);
						if("10001".equals(splitFee.get("FEE_CODE").toString()) 
								||"10017".equals(splitFee.get("FEE_CODE").toString())){
							continue;
						}
						
						if(splitFee.get("SPLITTING_RATIO")!=null){
							BigDecimal splitFeeRAmt = contractAmount.multiply(new BigDecimal(splitFee.get("SPLITTING_RATIO").toString())).setScale(2, RoundingMode.HALF_UP);
							splitFee.put("SPLITTING_FEE", splitFeeRAmt);
							splitFees.add(splitFee);
						}
					}
				}
				
				//由于四舍五入可能出现与总数对不上的情况，所以这里需要对最后一个数字进行修复
//				handAmount = handAmount.subtract(totalSplitAmt);
			}
			
			//客户到手金额
//			map.put("handAmount", handAmount);
		}
		return map;
	}

//	public static final String DICT_RELATIONSHIP = "relationShip";
	
//	public String dictMapping(String dictBefore,String dictName){
//		String dictMapping = "{\"relationShip\":{\"PARENT\":\"relationshipRelPar\",\"COUPLE\":\"relationshipRelCon\",\"CHILD\":\"relationshipRelKid\",\"FRIEND\":\"relationshipRelFri\",\"COLLEAGUE\":\"relationshipRelMat\",\"SIBLING\":\"relationshipRelRel\",\"COLLATERAL\":\"relationshipRelRel\"}}";
//		Map<String,Object> dictMaps = JsonUtil.convert(dictMapping, Map.class);
//		Map<String,Object> dictMap = MapUtils.getMap(dictMaps, dictName);
//		String dictAfter = MapUtils.getString(dictMap, dictBefore,"");
//		return dictAfter;
//	}
	
	
	@Override
	public Map<String, Object> queryFinalAuditResult(String appId) {
		Query query=Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply=mongoTemplate.findOne(query, LoanApply.class);
		Boolean isPass=false;
		String reason="";
		Map<String, Object> result=new HashMap<String, Object>();
		if(loanApply!=null){
			Map<String, Object> loanDate=loanApply.getLoanData();
			Map<String, Object> finalAudit=MapUtils.getMap(loanDate, "finalAudit");
			String auditResult=MapUtils.getString(finalAudit, "auditResult","");
			if(StringUtils.equals(auditResult, DictItem.PASS)){
				isPass=true;
			}else if(StringUtils.equals(auditResult, DictItem.REJECT)){
				reason=DictItem.FINAL_REJECT_REASON;
			}else{
				reason=DictItem.FINAL_NOT_RESULT;
			}
		}else{
			reason=DictItem.NO_LOAN_APPLY;
		}
		result.put("isPass", isPass);
		result.put("reason", reason);
		return result;
	}

	@Override
	public LoanApply pushLoanApply(String appId) {
		Query query=new Query();
		query.addCriteria(Criteria.where("appId").is(appId));
		LoanApply loanApply=mongoTemplate.findOne(query, LoanApply.class);
		return loanApply;
	}

	@Override
	public LoanNdesRelation findNdesRelation(String appId, String transactionId, String policyCode) {
		Query query=new Query();
		query.addCriteria(Criteria.where("appId").is(appId));
		query.addCriteria(Criteria.where("transactionId").is(transactionId));
		query.addCriteria(Criteria.where("policyCode").is(policyCode));
		LoanNdesRelation ndesRelation=mongoTemplate.findOne(query, LoanNdesRelation.class);
		return ndesRelation;
	}
}
