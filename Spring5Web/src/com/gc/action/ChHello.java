package com.gc.action;

public class ChHello implements Hello {

	private String msg = null;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public String doSalutation() {
		// TODO Auto-generated method stub
		return String.format("ÄãºÃ %s", this.msg);
	}

}
