package com.qf.cobra.pojo;

public class Role {

	private String roleId;
	private String roleName;
	private String roleDesc;
	private String roleEnName;
	
	public Role() {}

	public Role(String roleId, String roleName, String roleDesc, String roleEnName) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleDesc = roleDesc;
		this.roleEnName = roleEnName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleEnName() {
		return roleEnName;
	}

	public void setRoleEnName(String roleEnName) {
		this.roleEnName = roleEnName;
	}
	
}
