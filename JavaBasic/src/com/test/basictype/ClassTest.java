package com.test.basictype;

public class ClassTest {

	private String className;

	public ClassTest(String name) {
		this.setClassName(name);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public static class Builder {
		private String name;
		private int age;

		public Builder(int age, String name) {
			this.age = age;
			this.name = name;
		}

		public void TestBuild() {
			System.out.println(String.format("the name is%s the age is %s", name, age));
		}
	}
}
