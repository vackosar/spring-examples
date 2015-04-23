package com.vackosar.springexperiment.aspectj;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class AccountImpl implements Account {
	Logger log = Logger.getLogger(AccountImpl.class.getName());
	BigDecimal balance = new BigDecimal(0);

	@Override
	public void add(BigDecimal amount) {
		log.info("adding");
		this.balance.add(amount);
	}

	@Override
	public void substract(BigDecimal amount) {
		log.info("substracting");
		this.balance.subtract(amount);
	}

}
