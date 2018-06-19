package com.test.basictype;

public class RunnableClass implements Runnable  {

	public RunnableClass(){
		
	}
	
	public void run(){
		System.out.println("son Thread Id:"+Thread.currentThread().getId());
	}
}
