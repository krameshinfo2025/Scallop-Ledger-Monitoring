/**
 ***********************************************************************************
 AuthService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import com.scallop.ledger.response.ResponseData.LoginResponse;

/**
 * AuthService is an interface and
 * act as Business Logic layer for System User Operations 
 * 
 */
public interface AuthService {

	/**
	 * Request a staff token with a userName and password.
	 *
	 * @param login - the LoginRequest passed in the request body.
	 * @param actor - the authentication of type Authentication passed in the request header.
	 *
	 * @return loginResponse as response contains accesToken, tokenType and expiresIn.
	 */
	public LoginResponse login( String username,
			             String password);


}
