package com.vackosar.springexperiment.integration.quickstart;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Based on Spring Integration Quick Start example
 * @author kosar_v
 *
 */
public class App {
    public static void main(String... args) throws Exception {
        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("QuickStartIntegration.xml");

        TempConverter converter = ctx.getBean("simpleGateway", TempConverter.class);
        System.out.println(converter.fahrenheitToCelcius(68.0f));
    }
}
