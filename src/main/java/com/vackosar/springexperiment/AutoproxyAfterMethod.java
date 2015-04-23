package com.vackosar.springexperiment;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class AutoproxyAfterMethod  implements AfterReturningAdvice  {

	@Override
	public void afterReturning(Object arg0, Method arg1, Object[] arg2,
			Object arg3) throws Throwable {
		System.out.println(AutoproxyAfterMethod.class.getName() + ": Class " + arg3.getClass() +  " method " + arg1.getName() + " is returning.");
		
	}
	

}
