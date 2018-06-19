package com.test.basictype;

public class MutipleThreadClass extends Thread {
	private static int num=0;
	public MutipleThreadClass(){
		num++;
	}
	@Override
	public void run(){
		System.out.println("Main Threa Create the num is"+num+" Thread");
	}
}
