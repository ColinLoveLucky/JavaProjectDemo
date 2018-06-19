package com.qf.cobra.loan.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.qf.cobra.loan.service.notice.MailNotice;
import com.qf.cobra.loan.service.notice.Notice;
import com.qf.cobra.mail.SimpleMailService;

@Component
public class ErrorNoticeService {
	private Logger logger = LoggerFactory.getLogger(ErrorNoticeService.class);
	
	@Autowired
	public ThreadPoolTaskExecutor errorHandlerExecutor;
	@Autowired 
	private SimpleMailService simpleMailService;
	@Value("${mail.send.enable:true}")
	private Boolean sendEnable;
	
	public void notice(String appId, String title, String message, Throwable e) {
		if(sendEnable){
			// 邮件通知
			StringWriter sw = new StringWriter();
			if (null != e) {
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
			}
			executeNotice(new MailNotice(simpleMailService,title, String.format("借款编号：%s\r\n异常原因：%s\r\n异常堆栈：%s",appId, message, sw)));
		}else{
			logger.error("进件编号：{} 异常原因：{}",appId, message,e);
		}
		
	}
	/**
	 * 发送通知消息 <功能详细描述>
	 *
	 * @param notice
	 */
	private void executeNotice(Notice notice) {
		errorHandlerExecutor.execute(notice);
	}

}
