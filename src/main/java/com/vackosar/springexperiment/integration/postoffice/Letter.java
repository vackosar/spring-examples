package com.vackosar.springexperiment.integration.postoffice;

public class Letter {
private final String address;
private final Integer zipCode;
private final String text;

public Letter(String address, Integer zipCode, String text) {
	this.address = address;
	this.zipCode = zipCode;
	this.text = text;
}

public String getText() {
	return text;
}

public Integer getZipCode() {
	return zipCode;
}

public String getAddress() {
	return address;
}
}
