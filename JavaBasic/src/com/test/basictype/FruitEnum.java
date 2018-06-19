package com.test.basictype;

public enum FruitEnum implements Behaviour {
	APPLE("Æ»¹û", 1);
	private String name;
	private int index;

	private FruitEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	@Override
	public String getInfo() {
		return this.name();
	}

	@Override
	public void print() {
		System.out.println(this.index + "_" + this.name);
	}
}
