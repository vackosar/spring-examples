package com.vackosar.springexperiment.timer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;

public class App {
	private static final Logger log = Logger.getLogger(App.class.getName());
	public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
				"TimerSpringModule.xml");
        log.info("Sleeping.");
        try {
        	Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
