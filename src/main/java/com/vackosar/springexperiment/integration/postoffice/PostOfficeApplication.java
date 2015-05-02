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
		Letter letter = new Letter("Vaclav Kosar, The Prague Castle", 12000,
				"Dear king Vaclav, I love you.");
		postOffice.send(letter);
	}

	@MessagingGateway
	public interface PostOffice {

		@Gateway(requestChannel = "letters.input")
		void send(Letter letter);

	}

	@Bean
	public IntegrationFlow letters() {
		return f -> f.route(Letter::getZipCode, getZipcodeConsumer()).handle(
				m -> System.out.println(((Letter) m.getPayload()).getZipCode()));
	}
	
	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {                               
		return Pollers.fixedDelay(1000).get();
	}

	private Consumer<RouterSpec<MethodInvokingRouter>> getZipcodeConsumer() {
		return mapping -> mapping
				.subFlowMapping("12000", routeToPrague())
				.subFlowMapping("15000", routeToPrague());
	}
	
	private IntegrationFlow routeToPrague() {
		return sf -> sf                        
				  .channel(c -> c.queue(10))                            
				  .publishSubscribeChannel(c -> c                       
					  .subscribe(sub -> sub  
					      .<Letter, String> transform(letter -> "Deliver for zipcode: " + letter.getZipCode())
					      .handle(m -> System.out.println(m.getPayload()))
					   )
				   );
	}

	
}
