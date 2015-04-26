package com.vackosar.springexperiment.integration.quickstart;

import org.springframework.stereotype.Component;

@Component
public class TempConverterImpl implements TempConverter {

	@Override
	public float fahrenheitToCelcius(float fahren) {
		return (fahren - 32) / 9 * 5;
	}

}
