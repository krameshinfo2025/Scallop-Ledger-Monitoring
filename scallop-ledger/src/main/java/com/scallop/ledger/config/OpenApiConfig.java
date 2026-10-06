/**
 ***********************************************************************************
 OpenApiConfig.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Swagger UI at /swagger-ui.html. 
 * Log in via POST /api/auth/login, 
 * then press "Authorize" and paste the accessToken (without the "Bearer " prefix).
 */
@Configuration
public class OpenApiConfig {
	
	 public static final String BEARER_SCHEME = "bearer-jwt";

	    @Bean
	    OpenAPI fraudMonitorOpenApi() {
	        return new OpenAPI()
	                .info(new Info()
	                        .title("Scallop Ledger API")
	                        .version("v1")
	                        .description("""
                                    Scallop Ledger Double Entry Ledger Implementation.
                                    
	                                Public demo - synthetic data only, no real PII or money.

	                                Obtain a token from /api/v1/auth/login, then click Authorize and paste it.
	                                """))
	                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
	                .schemaRequirement(BEARER_SCHEME, new SecurityScheme()
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme("bearer")
	                        .bearerFormat("JWT"));
	    }

}
