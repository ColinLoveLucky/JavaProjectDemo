package com.qf.cobra.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "client")
public class HttpClientConfiguration {

	/** 最大连接数 */
	private int maxTotal;
	
	/** 同路由最大连接数 */
	private int maxPerRoute;
	
	/** 是否重试 */
	private boolean retry;
	
	/** 重试次数 */
	private int retrySize;
	
	/** 连接超时时间 */
	private int connectTimeout;
	
	/** 读取超时时间 */
	private int readTimeout;
	
	/** 获取连接时间 */
	private int connectionRequestTimeout;
}
