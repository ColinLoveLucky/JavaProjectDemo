package com.qf.cobra.loan.service.notice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qf.cobra.mail.SimpleMailService;

/**
 * 邮件通知
 * <功能详细描述>
 * @author ShaozeWang
 * @version [版本号, V1.0]
 * @since 2015年3月25日 下午6:25:21
 */
public class MailNotice extends Notice {
	private final static Logger logger = LoggerFactory
			.getLogger(MailNotice.class);
	
	private SimpleMailService simpleMailService;
	private String title;
	private String text;
 

	public MailNotice(SimpleMailService simpleMailService,String title, String text) {
		super();
		this.simpleMailService = simpleMailService;
		this.title = title;
		this.text = text;
	}


	@Override
	public void notice() {
		try {
			simpleMailService.sendMail(title, text);
		} catch (Exception e) {
			logger.error("发送邮件执行失败",e);
		}
	}
}
