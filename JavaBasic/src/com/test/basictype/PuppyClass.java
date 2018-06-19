package com.test.basictype;

public class PuppyClass {

	int puppyAge;
	
	public PuppyClass() {
	}

	public PuppyClass(String name) {
		System.out.println("the dog is name:" + name);
	}

	public void setAge(int age) {
		puppyAge = age;
	}
	
	public int getAge(){
		System.out.println("the dog is age:"+puppyAge);
		return puppyAge;
	}
}
