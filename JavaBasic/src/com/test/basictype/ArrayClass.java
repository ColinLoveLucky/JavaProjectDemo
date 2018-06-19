package com.test.basictype;

public class ArrayClass {
	public void testArray() {

		String[] strArray = new String[] { "Hello", "World", "Hi" };
		for (String item : strArray) {
			System.out.println("Item:" + item);
		}

		String[] cloneArray = strArray.clone();
		for (String item : cloneArray) {
			System.out.println("Clone Item:" + item);
		}
		System.out.println("strArray == cloneArray:" + strArray.equals(cloneArray));

	}
}
