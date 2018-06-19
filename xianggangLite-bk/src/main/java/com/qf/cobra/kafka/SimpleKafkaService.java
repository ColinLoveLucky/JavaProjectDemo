package com.qf.cobra.kafka;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import com.qf.cobra.loan.service.ErrorNoticeService;

/**
 * @author Cobra Team
 */
@Component
public class SimpleKafkaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleKafkaService.class);

	@SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
	@Autowired
	private ErrorNoticeService errorNoticeService;

    private final static Pattern KAFKA_MSG_PATTERN = Pattern.compile("(?<=\"appId\":\")(.*?)(?=\")");

    public void doSend(String topic, String msg) {
    	final String message = msg;
    	ListenableFuture<SendResult<String, String>>  result = kafkaTemplate.send(topic, msg);
    	result.addCallback(new SuccessCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				LOGGER.info("kafka数据发送成功,发送数据为:{}",message);
			}
		}, new FailureCallback() {
			@Override
			public void onFailure(Throwable ex) {
				LOGGER.error("kafka数据发送失败,发送数据为:{}",message,ex);
				String appId = null;
				Matcher matcher = KAFKA_MSG_PATTERN.matcher(msg);
				if (matcher.find()) {
					appId = StringUtils.trim(matcher.group(1));
				}
				errorNoticeService.notice(appId, "发送kafka数据失败", "发送kafka数据失败", ex);
			}
		});
    }
}
