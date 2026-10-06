/**
 ***********************************************************************************
 PasswordHashStartupCheck.Java
 version 1.0
 05,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.scallop.ledger.repo.AppUserRepository;

/**
 * Warns at startup about app users whose stored password hash is not valid
 * BCrypt (for example a seed hash with stray whitespace). BCryptPasswordEncoder
 * rejects such hashes, so those users always fail login with
 * INVALID_CREDENTIALS and nothing else points at the cause.
 */
@Component
public class PasswordHashStartupCheck {

	private static final Logger log = LoggerFactory.getLogger(PasswordHashStartupCheck.class);

	private final AppUserRepository appUserRepository;

	public PasswordHashStartupCheck(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void warnOnMalformedHashes() {
		List<String> usernames = appUserRepository.findUsernamesWithMalformedPasswordHash();
		if (!usernames.isEmpty()) {
			log.warn("{} app user(s) have a malformed BCrypt password hash and cannot log in: {}",
					usernames.size(), usernames);
		}
	}

}
