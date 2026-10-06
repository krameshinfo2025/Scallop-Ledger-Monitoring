/**
 ***********************************************************************************
 AppUserService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.domain.AppUser;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.CreateUserRequest;
import com.scallop.ledger.request.RequestData.UpdateUserRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.UserResponse;

/**
 * AppUserService is an interface act as business logic implementation
 * for App System User Operations
 * 
 */
public interface AppUserService {

	Optional<AppUser> findByUsername(String username);
	
	/**
	 * Method for create New App System User in the System.
	 * 
	 * @param createUserRequest - user input for create New App System User of type RequestData.CreateUserRequest.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any 
	 */
	public UserResponse create(CreateUserRequest createUserRequest, 
			                   String actor);

	/**
 	 * Method for Fetch All App System User in the System.
 	 * 
 	 * @param pageable - user input for Fetch all App System User of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the UserResponse List of type PagedResponse.UserResponse.
 	 * @throws LedgerException if any 
 	 */
	public PagedResponse<UserResponse> getAllAppUsers(Pageable pageable, 
			                                          String actor);

	
	/**
  	 * Method for Enable/disable a user or change their role of the App System User in the System.
  	 * 
  	 * @param id - a path variable and user identifier of type UUID.
  	 * @param updateUserRequest- user input for Update App System User of type RequestData.UpdateUserRequest.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the UserResponse of type ResponseData.UserResponse.
  	 * @throws LedgerException if any 
  	 */
	public UserResponse updateStatusOrRole(UUID id, 
			                               UpdateUserRequest updateUserRequest, 
			                               String actor);

	/**
   	 * Method for Enable/disable a user or change their role of the App System User in the System.
   	 * 
   	 * @param id - a path variable and user identifier of type UUID.
   	 * @param updateUserRequest- user input for Update App System User of type RequestData.ResetPasswordRequest.
   	 * @param actor - the authentication of type Authentication passed in the request header.
   	 *
     * @return 
   	 * @throws LedgerException if any 
   	 */
	public void resetPassword(UUID id, 
			                  String newPassword,
			                  String actor);


}
