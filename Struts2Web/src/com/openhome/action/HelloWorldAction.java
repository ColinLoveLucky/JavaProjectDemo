package com.openhome.action;

import com.opensymphony.xwork2.ActionSupport;

public class HelloWorldAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5857102704952895490L;

	public String execute() throws Exception {
		System.out.println("Excuting Action");
		return SUCCESS;
	}

}
