package com.qf.cobra.loan.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RequestMapping("/rest")
@RestController
@SuppressWarnings("rawtypes")
public class NdesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NdesController.class);

	@Autowired
	private LoanService loanService;
	@Autowired
	private IAuditService auditService;

	/**
	 * ndes进件规则回调 {"result":{"finalResult":"PASS"},
	 * "extId":"BR20170517150423732963", "policyCode":"QYJ_PH01",
	 * "transactionId":"52e3619927a24f3bb4966565acd6136e"}
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/receiveCallback", method = RequestMethod.POST)
	public ResponseData loanApply(HttpServletRequest request, @RequestBody Map map) {
		LOGGER.info("接收N-DES系统的回调,回调参数有:{}", map);
		ResponseData<LoanNdesRelation> responseData = ResponseUtil.defaultResponse();
		try {
			String appId = MapUtils.getString(map, "extId", "");
			String transactionId = MapUtils.getString(map, "transactionId", "");
			String policyCode = MapUtils.getString(map, "policyCode", "");
			String result = MapUtils.getString(MapUtils.getMap(map, "result"), "finalResult", "REJECT");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appId", appId);
			params.put("transactionId", transactionId);
			params.put("policyCode", policyCode);
			Map<String, Object> setParams = new HashMap<String, Object>();
			setParams.put("policyResult", result);
			setParams.put("policyTime", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
			// 修改LoanNdesRelation表
			LoanNdesRelation loanNdesRelation = loanService.queryAndUpdateNdes(params, setParams);
			if (loanNdesRelation != null) {
				// 返回规则结果时修改taskNodeInfo为COMPELETE_STATUS_DONE
				TaskNodeInfo taskNodeInfo = loanService.updateTaskStatus(appId,
						ReceiveServiceEnum.getEnumByPolicyCode(loanNdesRelation.getPolicyCode()));
				if (taskNodeInfo != null) {
					result = StringUtils.equals(result, "PASS") ? DictItem.VARIABLES_VALUE_PREAUDIT_PASS
							: DictItem.VARIABLES_VALUE_PREAUDIT_REJECT;

					if (DictItem.POLICY_BORROWER_LOAN_APPLY.equals(policyCode)) {
						if (DictItem.VARIABLES_VALUE_PREAUDIT_REJECT.equals(result)) {
							// 更新进件状态
							auditService.updateAppStatusNdes(appId, AppStatusEnum.LOANAPPLY_RULE_REJECT.getCode());
							// 保存操作历史
							auditService.saveLoanAuditHistory(appId, LoanAuditOperation.LOANAPPLY_RULE_REJECT);
						} else {
							// 更新进件状态
							auditService.updateAppStatusNdes(appId, AppStatusEnum.LOANAPPLY_RULE_SUCCESS.getCode());
							// 保存操作历史
							auditService.saveLoanAuditHistory(appId, LoanAuditOperation.LOANAPPLY_RULE_SUCCESS);
						}
						loanService.compeleteReciveTask(appId, taskNodeInfo.getProcessInstanceId(),
								taskNodeInfo.getTaskId(), result);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("接收N-DES系统回调结果失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("接收N-DES系统回调失败!");
		}
		return responseData;
	}
}
