package com.qf.cobra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.qf.cobra.interceptor.PermissionInterceptor;
import com.qf.cobra.interceptor.SessionInterceptor;
import com.qf.discovery.QfDiscoveryClientConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.charset.Charset;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@QfDiscoveryClientConfiguration
@EnableCircuitBreaker
@EnableAsync
@EnableSwagger2
public class App extends WebMvcConfigurerAdapter implements EmbeddedServletContainerCustomizer {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer arg0) {

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**").excludePathPatterns("/login",
//				"/reviceTask*", "/receiveCallback", "/rest/**", "/versionconfig/**", "/error", "/pushLoanApply/**",
//				"/loanTaskStatus/**", "/dict/refresh/**","/loan/loanDetail/**","/register/**","/forgotPwd","/mcmessage/**");
//		registry.addInterceptor(new PermissionInterceptor()).addPathPatterns("/audit/canncel");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		FormHttpMessageConverter formConvert = new FormHttpMessageConverter();
		formConvert.setCharset(Charset.forName("UTF-8"));
		converters.add(formConvert);
	}
}
