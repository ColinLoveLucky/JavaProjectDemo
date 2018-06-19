package com.qf.cobra.loan.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netfinworks.cert.domain.base.CheckVerifyResult;
import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.IBankCardVerifyService;
import com.qf.cobra.loan.service.IImagesService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;
import com.qf.cobra.qyjapi.service.IQyjapiService;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

/**
 * <录入申请信息> <控制层>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月18日 下午1:49:45
 */
@RestController
@RequestMapping("/applyInfo")
public class EnteringInformationsController {
	@Autowired
	IAuditService auditService;
	@Autowired
	IImagesService imageService;
	@Autowired
	private LoanService loanService;
	@Autowired
	IBankCardVerifyService bankCardService;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IKafkaService kafkaService;
	@Autowired
	private IQyjapiService qyjapiService;

	private static final Logger LOGGER = LoggerFactory.getLogger(EnteringInformationsController.class);

	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @Description 录入申请信息[包含影像文件上传]
	 * @author HongguangHu
	 * @param applyInfo
	 * @return
	 * @since 2017年4月19日 上午10:25:58
	 */
	@RequestMapping(value = "/entering")
	public ResponseData<?> enteringInformations(@RequestBody Map<String, Object> params) {
		@SuppressWarnings("rawtypes")
		ResponseData responseData = ResponseUtil.defaultResponse();
		try {
			String appId = (String) params.remove("appId");
			String isSave = (String) params.remove("isSave");
			responseData = imageService.enteringInformations(appId, params);
			// 若完成当前任务继续下一流程
			if (!StringUtils.equalsIgnoreCase(isSave, DictItem.YES)) {
				if (Objects.equals(ResponseCode.SUCCESS, responseData.getCode())) {
					// loanApp进件详情已通知
					TaskNodeInfo taskNodeInfo = loanService.updateTaskStatus(appId,
							ReceiveServiceEnum.BORROWER_LOAN_APPLY_DETAIL_LY);
					loanService.compeleteReciveTask(appId, taskNodeInfo.getProcessInstanceId(), taskNodeInfo.getTaskId(),
							DictItem.VARIABLES_VALUE_PREAUDIT_PASS);
				}
			} 
		} catch (Exception e) {
			LOGGER.error("信息录入失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("信息录入失败!");
		}
		return responseData;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/bankCard")
	public ResponseData<?> verifierBankCard(@RequestBody Map<String, Object> params) {
		ResponseData responseData = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> loanData = (Map<String, Object>) params.get("loanData");
			Map<String, Object> personalInfo = (Map<String, Object>) loanData.get("personalInfo");
			Map<String, Object> bankInfo = (Map<String, Object>) loanData.get("bankInfo");
			CheckVerifyResult cardVerify = null;
			if (personalInfo != null && bankInfo != null) {
				String cardType = "DR";
				String userName = MapUtils.getString(personalInfo, "name");
				String cardNo = MapUtils.getString(personalInfo, "idCard");
				String bankMobile = MapUtils.getString(bankInfo, "mobile");
				String bankCode = MapUtils.getString(bankInfo, "bankCode");
				String bankCardNo = MapUtils.getString(bankInfo, "accountNum");
				cardVerify = bankCardService.bankCardVerify(userName, cardNo, bankMobile, bankCode, cardType,
						bankCardNo, null, null);
			} else {
				cardVerify = new CheckVerifyResult();
				cardVerify.setSuccess(false);
				cardVerify.setResultMessage("验卡参数缺失！");
			}
			Map<String, Object> result = new HashMap<String, Object>();
			if (StringUtils.equalsIgnoreCase("S", cardVerify.getVerifyStatus())) {
				result.put("status", "true");
				result.put("result", "银行卡校验成功！");
			} else {
				result.put("result", cardVerify.getResultMessage());
				result.put("status", "false");
			}
			responseData.setData(result);
		} catch (Exception e) {
			LOGGER.error("银行卡校验失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("银行卡校验失败!");
		}
		return responseData;
	}
}
