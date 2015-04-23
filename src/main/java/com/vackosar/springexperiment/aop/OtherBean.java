package com.vackosar.springexperiment.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OtherBean {
	
	public OtherBean(HelloWorld helloWorld) {
		this.helloWorld = helloWorld;
	}
	
	@Autowired
	@Qualifier("helloBeanProxy")
	HelloWorld helloWorld;
	
	public void printHello () {
		helloWorld.printHelloName();
		System.out.println("Hello");
	}
}
