package com.gc.action;

public class ConstructorIoc {

	public String msg = null;

	public ConstructorIoc(String msg) {
		this.msg = msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
