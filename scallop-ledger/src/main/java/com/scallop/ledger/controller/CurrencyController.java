/**
 ***********************************************************************************
 CurrencyController.Java
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.UpdateCurrencyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.CurrencyResponse;
import com.scallop.ledger.service.LedgerAssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * an auto-complete endpoint to filter currency codes as the user types, 
 * and a paginated endpoint to return data filtered by the selected currency
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Currency Management")
public class CurrencyController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	/** The Place Holder for ledgerAssetService of type LedgerAssetService */
	private final LedgerAssetService ledgerAssetService;


	/**
	 * @param ledgerAssetService
	 */
	public CurrencyController(LedgerAssetService ledgerAssetService) {
		super();
		this.ledgerAssetService = ledgerAssetService;
	}
	
	
 	/**
 	 * Method for Fetch All Active Currencies in the System.
 	 * 
 	 * @param pageable - user input for Fetch all Active Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
     @GetMapping("/ledger-asset/getactive")
     @Operation(summary = "List all Active Currencies")
     public PagedResponse<CurrencyResponse> getActiveCurrencyList( 
    		            @RequestParam(defaultValue = "0") int page,
    		            @RequestParam(defaultValue = "5") int size,
    		            @RequestParam(defaultValue = "currency") String sortBy,
    		            @RequestParam(defaultValue = "true") boolean ascending,
    		            final Authentication authentication) {
    	 
    	 Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
         Pageable pageable = PageRequest.of(page, size, sort);
    	 
         return ledgerAssetService.getActiveCurrencyList(pageable,authentication.getName());
         
     }
     
 	/**
  	 * Method for Fetch All Currencies in the System.
  	 * 
 	 * @param pageable - user input for Fetch all Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
      @GetMapping("/ledger-asset/getall")
      @Operation(summary = "List all Currencies")
      public PagedResponse<CurrencyResponse> getAllCurrencyList( 
     		            @RequestParam(defaultValue = "0") int page,
     		            @RequestParam(defaultValue = "5") int size,
     		            @RequestParam(defaultValue = "currency") String sortBy,
     		            @RequestParam(defaultValue = "true") boolean ascending,
     		            final Authentication authentication) {
     	 
     	 Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
          Pageable pageable = PageRequest.of(page, size, sort);
     	 
          return ledgerAssetService.getAllCurrencyList(pageable,authentication.getName());
          
      }
      
   	/**
    	 * Method for Enable/disable a currency in the System.
    	 * 
    	 * @param currency - a path variable and currency identifier of type String.
    	 * @param updateCurrencyRequest- user input for Update Currency of type RequestData.UpdateCurrencyRequest.
    	 * @param authentication - the authentication of type Authentication passed in the request header.
    	 *
     	 * @return data containing the CurrencyResponse of type ResponseData.CurrencyResponse.
    	 * @throws LedgerException if any 
    	 */
       @PostMapping("/ledger-asset/{currency}/update")
       @Operation(summary = "Enable/disable a user or change their role; takes effect on their next request")
       public CurrencyResponse updateStatus(@PathVariable final String currency, 
      		                                @RequestBody final UpdateCurrencyRequest updateCurrencyRequest,
      		                                final Authentication authentication) {
      	 
           return ledgerAssetService.updateStatus(currency,updateCurrencyRequest,authentication.getName());
           
       }


}
