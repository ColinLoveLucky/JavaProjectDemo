/**
 *
 */
package com.qf.cobra.rabbitMQ;

import com.qf.cobra.config.McRabbitMqProperties;
import com.qf.cobra.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author LongjunLu
 */
@Component
public class RabbitProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitProducer.class);

  @Value("${spring.rabbitmq.host}")
  private String host;
  @Value("${spring.rabbitmq.port}")
  private String port;
  @Value("${spring.rabbitmq.username}")
  private String username;
  @Value("${spring.rabbitmq.password}")
  private String password;
  @Value("${spring.rabbitmq.virtual-host}")
  private String virtualHost;
  @Value("${spring.rabbitmq.sendVirtual-host}")
  private String sendVirtualHost;
  @Value("${spring.rabbitmq.qyjapiExchange}")
  private String qyjapiExchange;

  @Autowired
  private McRabbitMqProperties mcRabbitMqProperties;

  @Autowired
  BeanFactory beanFactory;

  // 创建rabbitTemplate 消息模板类
  @Bean(name = "qyjapiRabbitTemplate")
  public RabbitTemplate rabbitTemplate() {
    RabbitTemplate template = new RabbitTemplate(getConnectionFactory());
    template.setExchange(qyjapiExchange);
    return template;
  }

  private ConnectionFactory getConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setAddresses(host + ":" + port);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setVirtualHost(sendVirtualHost);
    connectionFactory.setPublisherConfirms(true); // 必须要设置

    return connectionFactory;
  }

  @Bean(name = "getXdtConnectionFactory")
  public ConnectionFactory getXdtConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setAddresses(host + ":" + port);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setVirtualHost(virtualHost);
    connectionFactory.setPublisherConfirms(true); // 必须要设置

    return connectionFactory;
  }

  // 创建MC消息rabbitTemplate 消息模板类
  @Bean(name = "mcRabbitTemplate")
  public RabbitTemplate mcRabbitTemplate(
      @Qualifier("mcRabbitMqConnectionFactory") ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(getMcRabbitMqConnectionFactory());
    return template;
  }

  /**
   * MC消息RabbitMQ容器
   */
  @Bean(name = "mcRabbitMqConnectionFactory")
  public ConnectionFactory getMcRabbitMqConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory
        .setAddresses(mcRabbitMqProperties.getHost() + ":" + mcRabbitMqProperties.getPort());
    connectionFactory.setUsername(mcRabbitMqProperties.getUsername());
    connectionFactory.setPassword(mcRabbitMqProperties.getPassword());
    connectionFactory.setVirtualHost(mcRabbitMqProperties.getVirtualHost());
    connectionFactory.setPublisherConfirms(true); // 必须要设置
    return connectionFactory;
  }

  public void sendMessage(MQBaseMessage message, String serviceName) {
    LOGGER.info("mq message: " + JsonUtil.convert(message));
    RabbitTemplate rabbitTemplate = beanFactory.getBean(serviceName, RabbitTemplate.class);
    rabbitTemplate.convertAndSend(message.getTopicName(), JsonUtil.convert(message));
  }
}
