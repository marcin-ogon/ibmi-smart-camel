package com.marcinogon.ibmismartcamel.config;

import java.util.Collections;

import org.apache.camel.spi.MaskingFormatter;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.support.processor.DefaultMaskingFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfiguration {

	@Bean
	Registry maskingRegistry() {
		MaskingFormatter valueMaskingFormatter = new DefaultMaskingFormatter(
				Collections.singleton("MASKME"),
				true,
				true,
				true);
		SimpleRegistry simpleRegistry = new SimpleRegistry();
		simpleRegistry.bind(MaskingFormatter.CUSTOM_LOG_MASK_REF, valueMaskingFormatter);
		return simpleRegistry;
	}
}
