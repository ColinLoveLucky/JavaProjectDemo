package com.test.basictype;

import java.io.Serializable;

public class SerializeClass implements Serializable {
	private String name;
	private char sex;
	private int year;
	private double gpa;

	public SerializeClass() {
	}
	
	public SerializeClass(String name,char sex,int year,double gpa){
		this.name=name;
		this.sex=sex;
		this.year=year;
		this.gpa=gpa;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	public void setSex(char sex){
		this.sex=sex;
	}
	public void setYear(int year){
		this.year=year;
	}
	public void setGpa(double gpa){
		this.gpa=gpa;
	}

}
