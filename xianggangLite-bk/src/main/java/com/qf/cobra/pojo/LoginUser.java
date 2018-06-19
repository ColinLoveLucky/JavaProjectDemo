package com.qf.cobra.pojo;

import java.util.List;
import java.util.Map;

public class LoginUser {

	private String userName;
	private String password;
	private String displayName;
	private String userId;
	private String token;
	private String expire;
	private String refreshToken;
	private String mail;
	private String title;
	private String city;
	private String store;
	private List<Map<String, Object>> menuList;
	private List<Role> roleList;
	private List<Map<String, Object>> storeList;
	private List<String> permList;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getExpire() {
		return expire;
	}
	public void setExpire(String expire) {
		this.expire = expire;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public List<Map<String, Object>> getMenuList() {
		return menuList;
	}
	public void setMenuList(List<Map<String, Object>> menuList) {
		this.menuList = menuList;
	}
	public List<Role> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	public List getStoreList() {
		return storeList;
	}
	public void setStoreList(List<Map<String, Object>> storeList) {
		this.storeList = storeList;
	}
	public List<String> getPermList() {
		return permList;
	}
	public void setPermList(List<String> permList) {
		this.permList = permList;
	}
	
}
