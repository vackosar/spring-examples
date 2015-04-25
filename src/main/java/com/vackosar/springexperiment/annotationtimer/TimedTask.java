package com.vackosar.springexperiment.annotationtimer;

import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Configuration
@Component
@EnableScheduling
public class TimedTask {
	Logger log = Logger.getLogger(TimedTask.class.getName());
	@Scheduled(fixedRate=500)
	public void logTimeout () {
		log.info("Timeout occured.");
	}
}
