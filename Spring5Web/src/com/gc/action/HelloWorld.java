package com.gc.action;

import java.util.Date;

public class HelloWorld {

	public String msg = null;
	public Date date = null;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return this.date;
	}

	public void cleanup() {
		this.msg = null;
		this.date = null;
		System.out.println("destory");
	}

}
