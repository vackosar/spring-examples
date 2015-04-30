package com.vackosar.springexperiment.configurationannotation;

import org.springframework.stereotype.Component;

@Component
public class User {
	String name;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
