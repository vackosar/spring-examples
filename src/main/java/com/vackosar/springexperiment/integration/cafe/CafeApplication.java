package com.vackosar.springexperiment.integration.cafe;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.stream.Collectors;

/**
 * Based on
 * https://spring.io/blog/2014/11/25/spring-integration-java-dsl-line-by-line-tutorial
 * http://docs.spring.io/spring-integration/reference/html/samples.html
 * https://github.com/spring-projects/spring-integration-samples/tree/master/applications/cafe/cafe-si
 * 
 * @author kosar_v
 *
 */

@SpringBootApplication               // 1
@IntegrationComponentScan            // 2
public class CafeApplication {

  public static void main(String[] args) throws Exception {
  	ConfigurableApplicationContext ctx =
  	              SpringApplication.run(CafeApplication.class, args);// 3

  	Cafe cafe = ctx.getBean(Cafe.class);                         // 4
  	for (int i = 1; i <= 100; i++) {                             // 5
       Order order = new Order(i);
       order.addItem(DrinkType.LATTE, 2, false); //hot
       order.addItem(DrinkType.MOCHA, 3, true);  //iced
       cafe.placeOrder(order);
  	}

  	System.out.println("Hit 'Enter' to terminate");              // 6
  	System.in.read();
  	ctx.close();
  }

  @MessagingGateway                                              // 7
  public interface Cafe {

  	@Gateway(requestChannel = "orders.input")                    // 8
  	void placeOrder(Order order);                                // 9

  }

  private AtomicInteger hotDrinkCounter = new AtomicInteger();

  private AtomicInteger coldDrinkCounter = new AtomicInteger();  // 10

  @Bean(name = PollerMetadata.DEFAULT_POLLER)
  public PollerMetadata poller() {                               // 11
  	return Pollers.fixedDelay(1000).get();
  }

  @Bean
  public IntegrationFlow orders() {                             // 12
  	return f -> f                                               // 13
  	  .split(Order.class, Order::getItems)                      // 14
  	  .channel(c -> c.executor(Executors.newCachedThreadPool()))// 15
  	  .<OrderItem, Boolean>route(
  			  OrderItem::isIced, mapping -> mapping // 16
  	    .subFlowMapping("true", routeIced())// 24
  	    .subFlowMapping("false", routeNotIced()))
  	  .transform(getOrderItemToDrinkTransformer())                                // 27
  	  .aggregate(getDrinksToDelivery(), null)     // 31
  	  .handle(CharacterStreamWritingMessageHandler.stdout());   // 32
  }

private Consumer<AggregatorSpec> getDrinksToDelivery() {
	return aggregator -> aggregator                       // 28
  	    .outputProcessor(groupDrinksToDelivery())                     // 30
  	    .correlationStrategy(m ->
  	      ((Drink) m.getPayload()).getOrderNumber());
}

private MessageGroupProcessor groupDrinksToDelivery() {
	return group ->                               // 29
	  new Delivery(group.getMessages()
	    .stream()
	    .map(message -> (Drink) message.getPayload())
	    .collect(Collectors.toList()));
}

private GenericTransformer<OrderItem, Drink> getOrderItemToDrinkTransformer() {
	return orderItem ->
  	    new Drink(orderItem.getOrderNumber(),
  	      orderItem.getDrinkType(),
  	      orderItem.isIced(),
  	      orderItem.getShots());
}

private IntegrationFlow routeNotIced() {
	return sf -> sf                        // 25
	  .channel(c -> c.queue(10))
	  .publishSubscribeChannel(c -> c
	    .subscribe(s ->
	      s.handle(m -> sleepUninterruptibly(5, TimeUnit.SECONDS)))// 26
	    .subscribe(sub -> sub
	      .<OrderItem, String>transform(item ->
	        Thread.currentThread().getName()
	          + " prepared hot drink #"
	          + this.hotDrinkCounter.incrementAndGet()
	          + " for order #" + item.getOrderNumber()
	          + ": " + item)
	      .handle(m -> System.out.println(m.getPayload()))));
}

private IntegrationFlow routeIced() {
	return sf -> sf                        // 17
	  .channel(c -> c.queue(10))                            // 18
	  .publishSubscribeChannel(c -> c                       // 19
	    .subscribe(s ->                                     // 20
	      s.handle(m -> sleepUninterruptibly(1, TimeUnit.SECONDS)))// 21
	    .subscribe(sub -> sub                               // 22
	      .<OrderItem, String>transform(item ->
	        Thread.currentThread().getName()
	          + " prepared cold drink #"
	          + this.coldDrinkCounter.incrementAndGet()
	          + " for order #" + item.getOrderNumber()
	          + ": " + item)                                 // 23
	      .handle(m -> System.out.println(m.getPayload()))));
}

private void sleepUninterruptibly(int i, TimeUnit seconds) {
	try {
		Thread.sleep(i * 1000);
	} catch (InterruptedException e) {
		// no nothing
	}
}

}