/**
 ***********************************************************************************
 AccountHolderController.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.AccountHolderListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.AccountHolderResponse;
import com.scallop.ledger.service.AccountHolderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * an auto-complete endpoint to filter Account Holder, 
 * and a paginated endpoint to return data filtered by the selected entityId
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Account Holder Management")
public class AccountHolderController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	/** The Place Holder for accountHolderService of type AccountHolderService */
	private final AccountHolderService accountHolderService;


	/**
	 * @param accountHolderService
	 */
	public AccountHolderController(AccountHolderService accountHolderService) {
		super();
		this.accountHolderService = accountHolderService;
	}
	
  	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param accountHolderListRequest - containing the AccountHolderListRequest List of type RequestData.AccountHolderListRequest.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the AccountHolderResponse List of type PagedResponse.AccountHolderResponse.
  	 * @throws LedgerException if any 
  	 */
	  @PostMapping("/account-holder")
      @Operation(summary = "List all Ledger Entities")
      public PagedResponse<AccountHolderResponse> getAllAccountHolders( 
     		            @RequestParam(defaultValue = "0") int page,
     		            @RequestParam(defaultValue = "5") int size,
     		            @RequestParam(defaultValue = "id") String sortBy,
     		            @RequestParam(defaultValue = "true") boolean ascending,
     		            @Valid @RequestBody AccountHolderListRequest accountHolderListRequest,
     		            final Authentication authentication) {
     	 
     	  Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
          Pageable pageable = PageRequest.of(page, size, sort);
     	 
          return accountHolderService.getAllAccountHolders(pageable,accountHolderListRequest,authentication.getName());
          
      }


}
