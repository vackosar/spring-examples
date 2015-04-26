package com.vackosar.springexperiment.integration.cafe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;

/**
 * Based on tutorial:
 * https://spring.io/blog/2014/11/25/spring-integration-java-dsl-line-by-line-tutorial
 * 
 * @author kosar_v
 *
 */
@Configuration
@EnableAutoConfiguration
@IntegrationComponentScan
public class UpcaseApp {

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = 
                                 SpringApplication.run(UpcaseApp.class, args);

		List<String> strings = Arrays.asList("foo", "bar");
		System.out.println(ctx.getBean(Upcase.class).upcase(strings));

		ctx.close();
	}

	@MessagingGateway
	public interface Upcase {

		@Gateway(requestChannel = "upcase.input")
		Collection<String> upcase(Collection<String> strings);

	}

	@Bean
	public IntegrationFlow upcase() {
	     return f -> f
		 	.split()                                         // 1
			.<String, String>transform(String::toUpperCase)  // 2
			.aggregate();                                    // 3
	}

}
