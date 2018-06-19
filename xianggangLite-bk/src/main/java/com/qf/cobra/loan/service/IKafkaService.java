package com.qf.cobra.loan.service;

import com.qf.cobra.pojo.LoanApply;

public interface IKafkaService {
	/**
	 * 发送进件信息给kafka <适用于执行n-des规则>
	 * @param loanInfo
	 * @param transactionId
	 * @param policyId
	 */
	public void sendLoanIn(LoanApply loanInfo,String transactionId,String policyId);
	/**
	 * 发送审核结果给kafka <适用于审核完成后>
	 * @param loanInfo
	 */
	public void sendLoanCredit(LoanApply loanInfo);
	/**
     * @Title: sendLoanApplyInfo 
     * @Description: 推送进件录入信息结果
     * @param @param loanInfo    设定文件 
     * @return void    返回类型 
     * @date 2017年7月12日 上午11:13:38
     * @author YabinLi
     * @throws
     */
    public void sendLoanApplyInfo(LoanApply loanInfo);
    /**
     * @Title: sendBorrowerLiteLoanApplyInfo
     * @Description:  推送borrowerlite进件信息
     * @param @param loanInfo    设定文件
     * @return void    返回类型
     * @date 2018年5月10日 下午16:51:38
     * @author YabinLi
     * @throws
     */
    public void sendBorrowerLiteLoanApplyInfo(LoanApply loanInfo);
	
}
