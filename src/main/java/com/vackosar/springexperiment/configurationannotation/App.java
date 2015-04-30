package com.vackosar.springexperiment.configurationannotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class App {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(App.class);
		ctx.refresh();

		User user = ctx.getBean("vackosar", User.class);
		System.out.println(user.getName());
	}

	@Bean
	public User vackosar() {
		return new User("vackosar");
	}
}
