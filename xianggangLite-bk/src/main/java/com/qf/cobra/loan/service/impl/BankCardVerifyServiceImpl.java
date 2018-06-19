package com.qf.cobra.loan.service.impl;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.netfinworks.cert.domain.base.CheckVerifyResult;
import com.netfinworks.cert.domain.request.BankCardCheckVerifyRequest;
import com.netfinworks.cert.facade.BankCardVerifyFacade;
import com.netfinworks.common.domain.Extension;
import com.qf.cobra.loan.service.IBankCardVerifyService;

/**
 * <银行卡校验> <功能详细描述>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月24日 下午11:01:16
 */
@Service
public class BankCardVerifyServiceImpl implements IBankCardVerifyService {
	@Value("${bank.verify.url}")
	private String bankVerifyUrl;
	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @Package com.quark.cobra.bizapp.loan.service.impl
	 * @Description 验证银行卡
	 * @author HongguangHu
	 * @param userName姓名
	 * @param cardNo身份证号
	 * @param bankMobile预留手机号
	 * @param bankCode银行编码
	 * @param cardType银行卡的类型借记卡
	 *            =DR;贷记卡=CR
	 * @param bankCardNo银行卡号
	 * @param verifyChannel验证渠道
	 *            民生就是CMBC 中金就是CPCN
	 * @param certOperation认证操作
	 *            默认不更新认证信息;实名认证= REALNAME;新增银行卡=ADD_BANK_CARD
	 * @return1-success 0-fail .[1]:resultMessage
	 * @since 2017年4月24日 下午10:03:51
	 */
	@Override
	public CheckVerifyResult bankCardVerify(String userName, String cardNo, String bankMobile, String bankCode, String cardType, String bankCardNo, String verifyChannel,
			String certOperation) {
		CheckVerifyResult verifyResult = null;
		try {
			// 创建WebService客户端代理工厂
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			// 注册WebService接口
			factory.setServiceClass(BankCardVerifyFacade.class);
			// 设置WebService地址
			factory.setAddress(bankVerifyUrl);
			BankCardVerifyFacade greetingService = (BankCardVerifyFacade) factory.create();
			Extension extension = new Extension();
			extension.add("cert_type", CertType.ID_CARD.toString());
			extension.add("cert_no", cardNo);
			extension.add("name", userName);
			extension.add("mobile", bankMobile);
			BankCardCheckVerifyRequest request = new BankCardCheckVerifyRequest();
			request.setAppId("cobra");
			// request.setMemberId("memberId");
			request.setBankCardNo(bankCardNo);
			request.setCardType(cardType);// 银行卡类型
			request.setBankCode(bankCode);
			request.setVerifyInfo(extension);
			// request.setVerifyChannel(verifyChannel);
			// request.setCertOperation(certOperation);
			verifyResult = greetingService.check(request);

			log.info(
					"IBankCardVerify.bankCardVerify is performed. the result is {}, userName = {} ,cardNo={},bankCode = {} ,bankCardNo = {} ,cardType = {} ,verifyChannel = {} ,certOperation = {}",
					verifyResult.getResultMessage() + "[" + verifyResult.getVerifyStatus() + "]", userName, cardNo, bankCode, bankCardNo,cardType, verifyChannel, certOperation);

		} catch (Exception e) {
			log.info("银行卡校验日志保存异常   ",e);
		} finally {
			try {
			} catch (Exception exc) {
				log.info("银行卡校验日志保存异常   " + exc.getMessage());
			}
		}
		return verifyResult;
	}
	/**
	 * 证件类型枚举
	 * 
	 * @author JiaweiZhang
	 *
	 */
	public enum CertType {
		ID_CARD("ID_CARD", "个人身份证");

		private String code;
		private String message;

		private CertType(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}

	/**
	 * 验证类型枚举
	 * 
	 * @author JiaweiZhang
	 *
	 */
	public enum VerifyType {
		REALNAME("REALNAME", "实名认证"), BANKCARD("ADD_BANK_CARD", "银行卡验证");
		// ,MOBILE("MOBILE","手机验证")
		// ,EMAIL("EMAIL","邮箱验证");

		private String code;
		private String message;

		private VerifyType(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}

	/**
	 * 验证状态枚举
	 */
	public enum VerifyStatus {
		S("S", "通过"), F("F", "不通过"), P("P", "处理中");

		private String code;
		private String message;

		private VerifyStatus(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}

	public enum ResponseCode {
		SUCCESS("1", "处理成功"), FAIL("0", "处理失败");

		private String code;
		private String message;

		private ResponseCode(String code) {
			this.code = code;
		}
		private ResponseCode(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}

	public enum BankCode {
		ICBC("ICBC", "工商银行"), ABC("ABC", "农业银行"), CCB("CCB", "建设银行"), BOC("BOC", "中国银行"), PSBC("PSBC", "邮储银行"), CMB("CMB", "招商银行"), CEB("CEB", "光大银行"), GDB("GDB", "广发银行"), HXB(
				"HXB", "华夏银行"), CIB("CIB", "兴业银行"), CITIC("CITIC", "中信银行"), SZPAB("SZPAB", "平安银行"), COMM("COMM", "交通银行"), SPDB("SPDB", "浦发银行"), LZBANK("LZBANK", "兰州银行"), BOS(
				"BOS", "上海银行"), CMBC("CMBC", "民生银行");

		private String code;
		private String message;

		private BankCode(String code) {
			this.code = code;
		}
		private BankCode(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}
}
