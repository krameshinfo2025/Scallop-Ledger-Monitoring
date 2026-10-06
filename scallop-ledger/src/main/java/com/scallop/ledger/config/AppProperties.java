/**
 ***********************************************************************************
 AppProperties.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 */
@ConfigurationProperties(prefix = "scallop.security")
public record AppProperties(Jwt jwt, Auth auth, BootstrapAdmin bootstrapAdmin) {

	/**
	 * Never null, so profiles or tests that omit scallop.security.jwt.* still
	 * start.
	 */
	public AppProperties {
		if (jwt == null) {
			jwt = new Jwt(null, null, null, null);
		}
	}

	public record Jwt(String secret, String issuer, Duration accessTokenTtl, Duration refreshTokenTtl) {
		
		public static final String DEFAULT_ISSUER = "scallop-ledger";
		public static final Duration DEFAULT_TOKEN_TTL = Duration.ofMinutes(15);

		public Jwt {
			if (issuer == null || issuer.isBlank()) {
				issuer = DEFAULT_ISSUER;
			}
			if (accessTokenTtl == null) {
				accessTokenTtl = DEFAULT_TOKEN_TTL;
			}
			if (refreshTokenTtl == null) {
				refreshTokenTtl = DEFAULT_TOKEN_TTL;
			}
		}
	}

	public record Auth(boolean requireVerifiedEmail, Duration emailVerificationTtl, Duration passwordResetTtl,
			String publicBaseUrl) {
	}

	public record BootstrapAdmin(String userName, String password) {
	}

}
