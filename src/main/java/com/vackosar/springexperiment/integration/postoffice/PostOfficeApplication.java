package com.vackosar.springexperiment.integration.postoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.RouterSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.router.MethodInvokingRouter;
import org.springframework.integration.scheduling.PollerMetadata;

/**
 * Post Office Spring Integration DSL Project. Submitted letter is routed
 * according to given ZIP code.
 *
 */
@SpringBootApplication
@IntegrationComponentScan
public class PostOfficeApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(
				PostOfficeApplication.class, args);
		PostOffice postOffice = ctx.getBean(PostOffice.class);
		Letter letter = new Letter("Prague Castle", 12000,
				"Dear King.");
		postOffice.send(letter);
		letter = new Letter("Spilberg Castle", 14000, "Dear King.");
		postOffice.send(letter);
	}

	@MessagingGateway
	public interface PostOffice {

		@Gateway(requestChannel = "letters.input")
		void send(Letter letter);

	}

	@Bean
	public IntegrationFlow letters() {
		return f -> f.route(Letter::getZipCode, getZipcodeConsumer());
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedDelay(1000).get();
	}

	private Consumer<RouterSpec<MethodInvokingRouter>> getZipcodeConsumer() {
		return mapping -> mapping.channelMapping("12000", "toPrague.input")
				.channelMapping("14000", "toBrno.input");
	}

	@Bean
	public IntegrationFlow toPrague() {
		return letterLogger("Prague");
	}

	@Bean
	public IntegrationFlow toBrno() {
		return letterLogger("Brno");
	}

	private IntegrationFlow letterLogger(String city) {
		return sf -> sf.<Letter, String> transform(
				letter -> "Deliver to " + city + " for zipcode: "
						+ letter.getZipCode()).handle(
				m -> System.out.println(m.getPayload()));
	}

}
