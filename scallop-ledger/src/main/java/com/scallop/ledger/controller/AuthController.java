/**
 ***********************************************************************************
 AuthController.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.request.RequestData.LoginRequest;
import com.scallop.ledger.response.ResponseData.LoginResponse;
import com.scallop.ledger.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Token endpoints. 
 * Both are credential exchanges that return a short-lived bearer token; 
 * no session or cookie is created. 
 * Errors use the OAuth2 error format (RFC 6749 section 5.2).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for authService of type AuthService */
	private final AuthService authService;

	/**
	 * AuthController Constructor 
	 * 
	 * @param users
	 * @param encoder
	 * @param integrationClients
	 * @param jwtTokenService
	 * @param betaConfiguration
	 * @param authService
	 * 
	 */
	public AuthController(final AuthService authService) {
		this.authService = authService;
	}
	
	/**
	 * Request a staff token with a userName and password.
	 *
	 * @param login - the LoginRequest passed in the request body.
	 *
	 * @return loginResponse as response contains accesToken, tokenType and expiresIn.
	 */
    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Exchange username and password for a bearer token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    	
    	LoginResponse loginResponse = authService.login(request.username(), request.password());
    	
    	ResponseEntity<LoginResponse>  response = ResponseEntity.ok()
	                                                            .cacheControl(CacheControl.noStore())
	                                                            .header(HttpHeaders.PRAGMA, "no-cache")
	                                                            .body(loginResponse);
        return response;
    }
    

}
