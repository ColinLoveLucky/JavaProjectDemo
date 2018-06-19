package com.qf.cobra.mail;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.HostInfo;

/**
 * 邮件发送服务 <功能详细描述>
 *
 */
@Component
public class SimpleMailService {
	private Logger logger = LoggerFactory.getLogger(SimpleMailMessage.class);

	@Autowired
	JavaMailSender javaMailSender;

	public void sendMail(String title, String text) {
		String mailId = UUID.randomUUID().toString();
		logger.info("开始发送邮件.mailId = {},title = {},text = {}", mailId, title,
				text);

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(emailTo.split(","));
		msg.setFrom(emailFrom);
		msg.setSubject(String.format("%s on %s on %s", title,
				HostInfo.hostinfo, DateUtil.formatCurrentDateTime()));
		msg.setText(text);
		javaMailSender.send(msg);

		logger.info("结束发送邮件.mailId = {}", mailId);
	}

	@Value("${mail.from}")
	private String emailFrom;

	@Value("${mail.to}")
	private String emailTo;

}
