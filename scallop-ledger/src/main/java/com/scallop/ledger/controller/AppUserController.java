/**
 ***********************************************************************************
 AppUserController.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.CreateUserRequest;
import com.scallop.ledger.request.RequestData.ResetPasswordRequest;
import com.scallop.ledger.request.RequestData.UpdateUserRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.UserResponse;
import com.scallop.ledger.service.AppUserService;

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
@RequestMapping("/api/v1")
@Tag(name = "App System User Management")
public class AppUserController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** The Place Holder for appUserService of type AppUserService */
	private final AppUserService appUserService;


	/**
	 * @param appUserService
	 */
	public AppUserController(AppUserService appUserService) {
		super();
		this.appUserService = appUserService;
	}
	
	/**
	 * Method for create New App System User in the System.
	 * 
	 * @param createUserRequest - user input for create New App System User of type RequestData.CreateUserRequest.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any 
	 */
     @PostMapping("/appuser")
     @SecurityRequirements
     @Operation(summary = "Add New App System User (ADMIN, ANALYST or CLIENT_SYSTEM)")
     public UserResponse create(@Valid @RequestBody final CreateUserRequest createUserRequest,
    		                                        final Authentication authentication) {
    	 
         return appUserService.create(createUserRequest,authentication.getName());
         
     }
     
     
 	/**
 	 * Method for Fetch All App System User in the System.
 	 * 
 	 * @param pageable - user input for Fetch all App System User of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the UserResponse List of type PagedResponse.UserResponse.
 	 * @throws LedgerException if any 
 	 */
     @GetMapping("/appuser")
     @Operation(summary = "List all App System Users")
     public PagedResponse<UserResponse> getAllAppUsers( 
    		            @RequestParam(defaultValue = "0") int page,
    		            @RequestParam(defaultValue = "5") int size,
    		            @RequestParam(defaultValue = "id") String sortBy,
    		            @RequestParam(defaultValue = "true") boolean ascending,
    		            final Authentication authentication) {
    	 
    	 Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
         Pageable pageable = PageRequest.of(page, size, sort);
    	 
         return appUserService.getAllAppUsers(pageable,authentication.getName());
         
     }

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
     @PostMapping("/appuser/{id}/enableorrole")
     @Operation(summary = "Enable/disable a user or change their role; takes effect on their next request")
     public UserResponse updateStatusOrRole(@PathVariable final UUID id, 
    		                                @RequestBody final UpdateUserRequest updateUserRequest,
    		                                final Authentication authentication) {
    	 
         return appUserService.updateStatusOrRole(id,updateUserRequest,authentication.getName());
         
     }

 	/**
   	 * Method for Enable/disable a user or change their role of the App System User in the System.
   	 * 
   	 * @param id - a path variable and user identifier of type UUID.
   	 * @param updateUserRequest- user input for Update App System User of type RequestData.ResetPasswordRequest.
   	 * @param authentication - the authentication of type Authentication passed in the request header.
   	 *
     * @return 
   	 * @throws LedgerException if any 
   	 */
     @PostMapping("/appuser/{id}/password")
     @Operation(summary = "Set a new password for a user")
     public void resetPassword(@PathVariable final UUID id, 
    		                                 @Valid @RequestBody final ResetPasswordRequest resetPasswordRequest,
    		                                 final Authentication authentication) {
    	 
    	 appUserService.resetPassword(id, resetPasswordRequest.newPassword(),authentication.getName());
     }


}
