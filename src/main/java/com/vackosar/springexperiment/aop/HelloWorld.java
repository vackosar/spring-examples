package com.vackosar.springexperiment.aop;

public class HelloWorld {
	private String name;
 
	public void setName(String name) {
		this.name = name;
	}
	
	public void printHelloName() {
		System.out.println("Hello ! " + name);
	}
}
