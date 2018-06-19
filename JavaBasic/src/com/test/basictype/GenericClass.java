package com.test.basictype;

import java.util.List;

public class GenericClass<T extends Object> {

	private T data;

	public GenericClass(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public T setData(List<? extends T> producer) {
		return this.data;
	}
}
