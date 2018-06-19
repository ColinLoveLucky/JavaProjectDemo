package com.openhome.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Login extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;

	public static final String SUCCESS = "success";
	public static final String ERROR = "error";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String execute() throws Exception {
		if (getUsername().equals("colin") && getPassword().equals("colin")) {
			ActionContext.getContext().getSession().put("user", getUsername());
			return SUCCESS;
		} else {
			return ERROR;
		}
	}

	public void validate() {
		if (getUsername() == null || getUsername().trim().equals("")) {
			addFieldError("username", "user.required");
		}
		if (getPassword() == null || getPassword().trim().equals("")) {
			addFieldError("password", "password.required");
		}

	}

}
