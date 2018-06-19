package com.qf.cobra.pojo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qf.cobra.util.ObjectIdSerializer;

/**
 * @author Cobra Team
 */
public class VersionConfig {
	@Id
	@JsonSerialize(using = ObjectIdSerializer.class) 
	private ObjectId id;
	private String tenantId;
	private String clientId;
	private String appVersion;
	private String processDefId;
	private String processVersion;
	private String createTime;
	private String updateTime;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
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
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getProcessDefId() {
		return processDefId;
	}
	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}
	public String getProcessVersion() {
		return processVersion;
	}
	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appVersion == null) ? 0 : appVersion.hashCode());
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((tenantId == null) ? 0 : tenantId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VersionConfig other = (VersionConfig) obj;
		if (appVersion == null) {
			if (other.appVersion != null) {
				return false;
			}
		} else if (!appVersion.equals(other.appVersion)) {
			return false;
		}
		if (clientId == null) {
			if (other.clientId != null) {
				return false;
			}
		} else if (!clientId.equals(other.clientId)) {
			return false;
		}
		if (tenantId == null) {
			if (other.tenantId != null) {
				return false;
			}
		} else if (!tenantId.equals(other.tenantId)) {
			return false;
		}
		return true;
	}
	
}
