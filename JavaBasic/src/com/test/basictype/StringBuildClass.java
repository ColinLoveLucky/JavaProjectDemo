package com.test.basictype;

public class StringBuildClass {
	public void testBuild(){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("Hello ");
		stringBuilder.append("World");
		stringBuilder.append("list");
		stringBuilder.insert(6, "I'm wannna be free");
		String result=stringBuilder.toString();
		System.out.println(result);
	}
}
