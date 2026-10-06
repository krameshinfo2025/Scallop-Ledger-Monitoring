/**
 ***********************************************************************************
 LedgerEntitiyController.Java
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.CreateLedgerEntitiyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEntiryResponse;
import com.scallop.ledger.service.LedgerEntityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * an auto-complete endpoint to filter currency codes as the user types, 
 * and a paginated endpoint to return data filtered by the selected entityId
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ledger Entity Management")
public class LedgerEntityController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	/** The Place Holder for ledgerEntitiyService of type LedgerEntitiyService */
	private final LedgerEntityService ledgerEntitiyService;


	/**
	 * @param ledgerEntitiyService
	 */
	public LedgerEntityController(LedgerEntityService ledgerEntitiyService) {
		super();
		this.ledgerEntitiyService = ledgerEntitiyService;
	}
	
	/**
	 * Method for create New Ledger Entity in the System.
	 * 
	 * @param createLedgerEntitiyRequest - user input for create Ledger Entity of type RequestData.CreateLedgerEntitiyRequest.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any 
	 */
     @PostMapping("/ledger-entity")
     @SecurityRequirements
     @Operation(summary = "Add New Ledger Entity.")
     public LedgerEntiryResponse create(@Valid @RequestBody final CreateLedgerEntitiyRequest createLedgerEntitiyRequest,
    		                                        final Authentication authentication) {
    	 
         return ledgerEntitiyService.create(createLedgerEntitiyRequest,authentication.getName());
         
     }
     
  	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the LedgerEntiryResponse List of type PagedResponse.LedgerEntiryResponse.
  	 * @throws LedgerException if any 
  	 */
      @GetMapping("/ledger-entity")
      @Operation(summary = "List all Ledger Entities")
      public PagedResponse<LedgerEntiryResponse> getAllAppUsers( 
     		            @RequestParam(defaultValue = "0") int page,
     		            @RequestParam(defaultValue = "5") int size,
     		            @RequestParam(defaultValue = "entityId") String sortBy,
     		            @RequestParam(defaultValue = "true") boolean ascending,
     		            final Authentication authentication) {
     	 
     	 Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
          Pageable pageable = PageRequest.of(page, size, sort);
     	 
          return ledgerEntitiyService.getAllLedgerEntities(pageable,authentication.getName());
          
      }



}
