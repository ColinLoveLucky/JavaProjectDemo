package com.qf.cobra.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbit mq配置类
 * 
 * @author BZhang
 *
 */
@Configuration
public class RabbitConfig {

	@Bean(name = "rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(@Qualifier("getXdtConnectionFactory") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setErrorHandler(new ConditionalRejectingErrorHandler(new ConditionalRejectingErrorHandler.DefaultExceptionStrategy(){
			@Override
			protected boolean isUserCauseFatal(Throwable cause) {
				return true;
			}
		}));
		return factory;
	}
}
