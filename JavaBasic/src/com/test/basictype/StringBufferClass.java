package com.test.basictype;

public class StringBufferClass {
	public void testBuffer() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Hello").append("World").append("List");
		stringBuffer.reverse();
		String result=stringBuffer.toString();
		System.out.println(result);
	}
}
