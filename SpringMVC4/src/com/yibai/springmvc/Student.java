package com.yibai.springmvc;

import javax.validation.constraints.Max;

public class Student {

	@Max(value = 1, message = "{error.age}")
	private Integer age;
	private String name;
	private Integer id;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}