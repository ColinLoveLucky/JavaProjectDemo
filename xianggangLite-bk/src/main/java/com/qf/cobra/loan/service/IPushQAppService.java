package com.qf.cobra.loan.service;

import java.util.Map;

import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;

/**
 * <和申请系统交互> <功能详细描述>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月19日 下午6:29:44
 */
public interface IPushQAppService {

	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description 推送申请系统
	 * @author HongguangHu
	 * @param appId
	 * @return
	 * @since 2017年4月19日 下午6:27:58
	 */
	public void pushQApp(String appId);
	
	/**
	 * 查询终审结果
	 * @param appId
	 */
	public Map<String, Object> queryFinalAuditResult(String appId);
	
	public LoanApply pushLoanApply(String appId);
	
	public LoanNdesRelation findNdesRelation(String appId,String transactionId,String policyCode);

}
