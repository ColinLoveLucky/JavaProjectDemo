package com.qf.cobra.qapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "qapp")
public class QappProperties {
	private String queryUrl;
	private String pushUrl;
	private Long cacheTime;
	private String querySwitch;
	private String queryXDUrl;
	
	public String getQueryUrl() {
		return queryUrl;
	}
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}
	public String getPushUrl() {
		return pushUrl;
	}
	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}
	public Long getCacheTime() {
		return cacheTime;
	}
	public void setCacheTime(Long cacheTime) {
		this.cacheTime = cacheTime;
	}
	public String getQuerySwitch() {
		return querySwitch;
	}
	public void setQuerySwitch(String querySwitch) {
		this.querySwitch = querySwitch;
	}
	public String getQueryXDUrl() {
		return queryXDUrl;
	}
	public void setQueryXDUrl(String queryXDUrl) {
		this.queryXDUrl = queryXDUrl;
	}
}
