package com.qf.cobra.loan.service;

import com.netfinworks.cert.domain.base.CheckVerifyResult;

/**
 * <验证银行卡>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月24日 下午11:01:09
 */
public interface IBankCardVerifyService {

	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description 验证银行卡
	 * @author HongguangHu
	 * @param userName姓名
	 * @param cardNo身份证号
	 * @param bankMobile预留手机号
	 * @param bankCode银行编码
	 * @param cardType银行卡类型
	 *            [借记卡=DR;贷记卡=CR]
	 * @param bankCardNo银行卡号
	 * @param verifyChannel验证渠道
	 * @param certOperation认证操作
	 *            默认不更新认证信息;实名认证= REALNAME;新增银行卡=ADD_BANK_CARD
	 * @return[0]:1-success 0-fail .[1]:resultMessage
	 * @since 2017年4月24日 下午10:05:40
	 */
	public CheckVerifyResult bankCardVerify(String userName, String cardNo, String bankMobile, String bankCode, String cardType, String bankCardNo, String verifyChannel,
			String certOperation);

}
