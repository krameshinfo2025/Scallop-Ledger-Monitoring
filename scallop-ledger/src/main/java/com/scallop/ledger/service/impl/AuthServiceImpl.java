/**
 ***********************************************************************************
 AuthServiceImpl.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scallop.ledger.config.JwtTokenService;
import com.scallop.ledger.domain.AppUser;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.response.ResponseData.LoginResponse;
import com.scallop.ledger.service.AppUserService;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.AuthService;

/**
 * AuthServiceImpl is a class and act as Business Logic Implementation layer for
 * System User Operations
 * 
 */
@Service(value = "authService")
public class AuthServiceImpl implements AuthService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for encoder of type PasswordEncoder */
	private final PasswordEncoder encoder;

	/** The Place Holder for jwtTokenService of type JwtTokenService */
	private final JwtTokenService jwtTokenService;

	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/** The Place Holder for appUserService of type AppUserService */
	private final AppUserService appUserService;

	/**
	 * Compared against when the userName does not exist, so an unknown user costs
	 * the same BCrypt work as a wrong password and response timing does not reveal
	 * which userNames are real.
	 */
	private final String dummyHash;

	/**
	 * @param encoder
	 * @param jwtTokenService
	 * @param auditService
	 * @param appUserService
	 */
	public AuthServiceImpl(PasswordEncoder encoder, JwtTokenService jwtTokenService, AuditService auditService,
			AppUserService appUserService) {
		this.encoder = encoder;
		this.jwtTokenService = jwtTokenService;
		this.auditService = auditService;
		this.appUserService = appUserService;
		this.dummyHash = encoder.encode("timing-equaliser-not-a-real-password");
	}

	@Override
	public LoginResponse login(final String username, final String password) {

		Optional<AppUser> appUser = appUserService.findByUsername(username);

		boolean passwordOk = encoder.matches(password, appUser.map(AppUser::getPasswordHash).orElse(dummyHash));

		if (appUser.isEmpty() || !passwordOk || !appUser.get().isEnabled()) {

			auditService.recordIndependently(username, "LOGIN_FAILED", "app_user",
					appUser.map(AppUser::getId).orElse(null), null);

			// One message for every cause: unknown user, wrong password, disabled.
			throw new LedgerException(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS",
					"Username or password is incorrect.");
		}

		AppUser user = appUser.get();

		auditService.recordIndependently(user.getUsername(), "LOGIN_SUCCEEDED", "app_user", user.getId(), null);

		LoginResponse response = jwtTokenService.issueTokens(user);

		return response;
	}

}
