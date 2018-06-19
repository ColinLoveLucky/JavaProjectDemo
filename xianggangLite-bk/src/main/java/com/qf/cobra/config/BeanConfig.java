package com.qf.cobra.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class BeanConfig {
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(50);
		taskExecutor.setQueueCapacity(100);
		taskExecutor
				.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.initialize();
		return taskScheduler;
	}

	// @Bean
	// public EmbeddedServletContainerCustomizer containerCustomizer() {
	// return new EmbeddedServletContainerCustomizer() {
	// @Override
	// public void customize(ConfigurableEmbeddedServletContainer container) {
	// container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,
	// "/error/404"));
	// container.addErrorPages(new ErrorPage(
	// HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
	// container.addErrorPages(new ErrorPage(
	// java.lang.Throwable.class, "/error/500"));
	// }
	// };
	// }

	@Value("${maxConnPerRoute:2000}")
	private int maxConnPerRoute;
	@Value("${maxConnTotal:4000}")
	private int maxConnTotal;

	@Bean
	public HttpClient getHttpClient() {
		CloseableHttpClient ret = HttpClients.custom()
				.setMaxConnPerRoute(maxConnPerRoute)
				.setMaxConnTotal(maxConnTotal).disableCookieManagement()
				.build();
		return ret;
	}

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(
				getHttpClient()));
	}
	
	/**
	 * webSocket使用
	 * @return
	 */
	  @Bean
	    public ServerEndpointExporter serverEndpointExporter(){
	        return new ServerEndpointExporter();
	    }
}
