package com.openhome.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class BooksAction implements Action {

	private String[] books;

	public void setBooks(String[] books) {
		this.books = books;
	}

	public String[] getBooks() {
		return books;
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		String user = (String) ActionContext.getContext().getSession().get("user");
		if (user != null && user.equals("colin")) {
			BookService bs = new BookService();
			setBooks(bs.getLeeBooks());
			return SUCCESS;
		} else
			return LOGIN;
	}
}
