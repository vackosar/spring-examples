package com.vackosar.springexperiment.aspectj;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AccountAspect {
	Logger log = Logger.getLogger(AccountAspect.class.getName());
	
	@Before("execution(* com.vackosar.springexperiment.aspectj.Account.add(..))")
	public void logBefore(JoinPoint joinPoint) {
		log.info("Intercepted method: " + joinPoint.getSignature().getName());
	}
	
	
}
