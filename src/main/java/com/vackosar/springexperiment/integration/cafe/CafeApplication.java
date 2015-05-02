package com.vackosar.springexperiment.integration.cafe;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.Channels;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.RouterSpec;
import org.springframework.integration.dsl.channel.MessageChannelSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.router.MethodInvokingRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.integration.transformer.GenericTransformer;

/**
 * Based on
 * https://spring.io/blog/2014/11/25/spring-integration-java-dsl-line-by-line-tutorial
 * http://docs.spring.io/spring-integration/reference/html/samples.html
 * https://github.com/spring-projects/spring-integration-samples/tree/master/applications/cafe/cafe-si
 * 
 * @author kosar_v
 *
 */

@SpringBootApplication               
@IntegrationComponentScan            
public class CafeApplication {

public static void main(String[] args) throws Exception {
	ConfigurableApplicationContext ctx =
	              SpringApplication.run(CafeApplication.class, args);

	Cafe cafe = ctx.getBean(Cafe.class);                         
	for (int i = 1; i <= 100; i++) {                             
		Order order = new Order(i);
		order.addItem(DrinkType.LATTE, 2, false); 
		order.addItem(DrinkType.MOCHA, 3, true);  
		cafe.placeOrder(order);
	}

	System.out.println("Hit 'Enter' to terminate");              
	System.in.read();
	ctx.close();
}

@MessagingGateway                                              
public interface Cafe {

	@Gateway(requestChannel = "orders.input")                    
	void placeOrder(Order order);                                

}

private AtomicInteger hotDrinkCounter = new AtomicInteger();

private AtomicInteger coldDrinkCounter = new AtomicInteger();  

@Bean(name = PollerMetadata.DEFAULT_POLLER)
public PollerMetadata poller() {                               
	return Pollers.fixedDelay(1000).get();
}

@Bean
public IntegrationFlow orders() {                             
	return f -> f                                               
	  .split(Order.class, Order::getItems)                      
	  .channel(getThreadPoolMessageChannelSpec())
	  .route(OrderItem::isIced, getIsIcedConsumer())
	  .transform(getOrderItemToDrinkTransformer())                                
	  .aggregate(getDrinksToDelivery(), null)     
	  .handle(CharacterStreamWritingMessageHandler.stdout());   
}

private Function<Channels, MessageChannelSpec<?, ?>> getThreadPoolMessageChannelSpec() {
	return c -> c.executor(Executors.newCachedThreadPool());
}

private Consumer<RouterSpec<MethodInvokingRouter>> getIsIcedConsumer() {
	return mapping -> mapping 
	    .subFlowMapping(Boolean.TRUE.toString(), routeIced())
	    .subFlowMapping(Boolean.FALSE.toString(), routeNotIced());
}

private Consumer<AggregatorSpec> getDrinksToDelivery() {
	return aggregator -> aggregator                       
	    .outputProcessor(groupDrinksToDelivery())                     
	    .correlationStrategy(m ->
	      ((Drink) m.getPayload()).getOrderNumber());
}

private MessageGroupProcessor groupDrinksToDelivery() {
	return group ->                               
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
	return sf -> sf                        
	  .channel(c -> c.queue(10))
	  .publishSubscribeChannel(c -> c
	    .subscribe(s ->
	      s.handle(m -> sleepUninterruptibly(5, TimeUnit.SECONDS)))
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
	return sf -> sf                        
	  .channel(c -> c.queue(10))                            
	  .publishSubscribeChannel(c -> c                       
	    .subscribe(s ->                                     
	      s.handle(m -> sleepUninterruptibly(1, TimeUnit.SECONDS)))
	    .subscribe(sub -> sub                               
	      .<OrderItem, String>transform(item ->
	        Thread.currentThread().getName()
	          + " prepared cold drink #"
	          + this.coldDrinkCounter.incrementAndGet()
	          + " for order #" + item.getOrderNumber()
	          + ": " + item)                                 
	      .handle(m -> System.out.println(m.getPayload()))));
}

private void sleepUninterruptibly(int i, TimeUnit seconds) {
	try {
		Thread.sleep(i * 1000);
	} catch (InterruptedException e) {
		// do nothing
	}
}

}