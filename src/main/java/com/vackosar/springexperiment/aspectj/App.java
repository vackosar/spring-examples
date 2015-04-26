package com.vackosar.springexperiment.aspectj;

import java.math.BigDecimal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Based on Mkyong tutorial
 * @author kosar_v
 *
 */
public class App {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"AspectJSpringModule.xml");
		Account account = (Account) context.getBean("account");
		account.add(new BigDecimal(200));
	}
}
