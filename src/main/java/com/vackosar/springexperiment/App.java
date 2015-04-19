package com.vackosar.springexperiment;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ApplicationContext context = new ClassPathXmlApplicationContext(
				"Spring-Module.xml");
 
		HelloWorld obj = (HelloWorld) context.getBean("helloBean");
		obj.printHelloName();
		obj.setName("Test Value 2.");
		obj.printHelloName();
		HelloWorld obj2 = (HelloWorld) context.getBean("helloBean");
		obj2.printHelloName();
		
		OtherBean obj3 = (OtherBean) context.getBean("otherBean");
		obj3.printHello();
		
		OtherBean obj4 = (OtherBean) context.getBean("otherBeanProxy");
		obj4.printHello();
    }
}
