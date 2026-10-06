/**
 ***********************************************************************************
 JacksonConfig.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A JacksonConfig class is used to customize the behavior of the ObjectMapper, 
 * which is the core engine responsible for converting Java objects 
 * to JSON (serialization) and vice versa (de-serialization).
 * 
 */
@Configuration
public class JacksonConfig {
	
	@Bean
    public ObjectMapper objectMapper() {
		// Journal requests carry Instants (occurredAt); write them as ISO-8601 text,
		// which also feeds the idempotency hash and the stored request JSON.
		ObjectMapper mapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         return mapper;
    }

}
