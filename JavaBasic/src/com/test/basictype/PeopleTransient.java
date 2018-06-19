package com.test.basictype;

import java.io.Serializable;

public class PeopleTransient implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	transient int age;

	public PeopleTransient(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String toString() {
		return "name=" + this.name + ",age=" + this.age;
	}
}
