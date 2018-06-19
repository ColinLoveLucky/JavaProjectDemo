package com.qf.cobra.loanapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "loanapp")
public class LoanappProperties {
	private String appIdUrl;
	private String applyUrl;
	private String tenantId;
	private String clientId;
	private String productId;
	private String apiKey;
	private String username;
	private String casClientId;
	private String password;
	private String tokenUrl;

	public String getAppIdUrl() {
		return appIdUrl;
	}
	public void setAppIdUrl(String appIdUrl) {
		this.appIdUrl = appIdUrl;
	}
	public String getApplyUrl() {
		return applyUrl;
	}
	public void setApplyUrl(String applyUrl) {
		this.applyUrl = applyUrl;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCasClientId() {
		return casClientId;
	}

	public void setCasClientId(String casClientId) {
		this.casClientId = casClientId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
}
