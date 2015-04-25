package com.vackosar.springexperiment.timer;

import java.util.logging.Logger;

public class TimedTask {
	Logger log = Logger.getLogger(TimedTask.class.getName());
	public void logTimeout () {
		log.info("Timeout occured.");
	}
}
