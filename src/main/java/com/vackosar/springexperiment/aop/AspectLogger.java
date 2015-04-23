package com.vackosar.springexperiment.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;

public class AspectLogger {
	@Before("execution(* com.mkyong.customer.bo.CustomerBo.addCustomer(..))")
	public void logBefore(JoinPoint joinPoint) {
 
		System.out.println("logBefore() is running!");
		System.out.println("hijacked : " + joinPoint.getSignature().getName());
		System.out.println("******");
	}
}
