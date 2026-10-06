/**
 ***********************************************************************************
 SecurityConfig.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Stateless JWT security: 
 * this service issues HS256 access tokens itself 
 * and validates them as an OAuth2 resource server. 
 * No sessions, no form login, no HTTP Basic.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class SecurityConfig {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	public static final String ROLES_CLAIM = "roles";

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.httpBasic(basic -> basic.disable())
			.formLogin(form -> form.disable())
			.logout(logout -> logout.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(
			   auth -> auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
				           .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				           .requestMatchers("/actuator/health", "/actuator/info", "/error").permitAll()
				           .requestMatchers("/api/v1/appuser","/api/v1/appuser/**",
				        		             "/api/v1/ledger-asset","/api/v1/ledger-asset/**",
				        		             "/api/v1/ledger-entity","/api/v1/ledger-entity/**",
				        		             "/api/v1/quotes","/api/v1/quotes/**",
				        		             "/api/v1/reconciliation","/api/v1/reconciliation/**",
				        		             "/api/v1/account-holder","/api/v1/account-holder/**",
				        		             "/api/v1/wallets","/api/v1/wallets/**",
				        		             "/api/v1/ledger-entity","/api/v1/ledger-entity/**",
				        		             "/api/v1/beneficiaries","/api/v1/beneficiaries/**",
				        		             "/api/v1/payments","/api/v1/payments/**")
				           .hasRole("ADMIN")
				           .anyRequest().authenticated())
			.oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		return http.build();
	}

	@Bean
	SecretKey jwtSigningKey(AppProperties props) {
		
		String secret = props.jwt().secret();
		if (secret == null || secret.isBlank()) {
			log.warn("app.jwt.secret (JWT_SECRET) is not set - using a random key. Tokens will not survive a restart.");
			byte[] random = new byte[32];
			new SecureRandom().nextBytes(random);
			return new SecretKeySpec(random, "HmacSHA256");
		}
		byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("app.jwt.secret must be at least 32 bytes for HS256");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
		
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey));
		
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSigningKey, AppProperties props) {
		
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSigningKey)
			.macAlgorithm(MacAlgorithm.HS256)
			.build();
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(props.jwt().issuer())));
		
		return decoder;
	}

	/** Maps the {@code roles} claim (e.g. ["ADMIN"]) to Spring authorities (ROLE_ADMIN). */
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName(ROLES_CLAIM);
		authorities.setAuthorityPrefix("ROLE_");
		
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		
		return converter;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
}
