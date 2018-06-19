package com.qf.cobra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MC消息RabbitMq配置文件
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 17:12
 */
@Configuration
@ConfigurationProperties(prefix = "mc.rabbitmq")
public class McRabbitMqProperties {

	/** IP地址 */
	private String host;
	
	/** 端口号 */
	private String port;
	
	/** 用户名 */
	private String username;
	
	/** 密码 */
	private String password;
	
	/** VirtualHost */
	private String virtualHost;
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the virtualHost
	 */
	public String getVirtualHost() {
		return virtualHost;
	}

	/**
	 * @param virtualHost the virtualHost to set
	 */
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	
}
