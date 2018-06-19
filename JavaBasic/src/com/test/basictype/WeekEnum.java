package com.test.basictype;

public enum WeekEnum {
	MON("÷‹“ª", 1);
	private String name;
	private int index;

	private WeekEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	@Override
	public String toString() {
		return this.index + "_" + this.name;
	}
}
