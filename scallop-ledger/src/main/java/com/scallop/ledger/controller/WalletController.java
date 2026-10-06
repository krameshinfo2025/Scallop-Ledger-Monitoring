/**
 ***********************************************************************************
 WalletController.Java
 version 1.0
 25,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.OpenWalletRequest;
import com.scallop.ledger.request.RequestData.WalletListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.WalletResponse;
import com.scallop.ledger.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Wallet Management")
public class WalletController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for walletService of type  WalletService */
    private final WalletService walletService;

	/**
	 * WalletController Constructor 
	 * 
	 * @param walletService
	 * 
	 */
    public WalletController(final WalletService walletService ) {
        this.walletService = walletService;
    }
    
 	/**
  	 * Method for Fetch All Wallets in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param walletListRequest - containing the WalletListRequest List of type RequestData.WalletListRequest.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the WalletResponse List of type PagedResponse.WalletResponse.
  	 * @throws LedgerException if any 
  	 */
	  @PostMapping("/wallets")
      @Operation(summary = "List all Wallets")
      public PagedResponse<WalletResponse> getAllWallets( 
     		            @RequestParam(defaultValue = "0") int page,
     		            @RequestParam(defaultValue = "5") int size,
     		            @RequestParam(defaultValue = "entityId") String sortBy,
     		            @RequestParam(defaultValue = "true") boolean ascending,
     		            @Valid @RequestBody WalletListRequest walletListRequest,
     		            final Authentication authentication) {
     	 
     	  Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
          Pageable pageable = PageRequest.of(page, size, sort);
     	 
          return walletService.getAllWallets(pageable,walletListRequest,authentication.getName());
          
      }


	/**
	 * Request Open a wallet in one currency, with its ledger account.
	 *
	 * @param request - the OpenWalletRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 * 
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
    @PostMapping("/wallets/open")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Open a wallet in one currency, with its ledger account")
    public WalletResponse open( @Valid @RequestBody final OpenWalletRequest openWalletRequest,
                                final Authentication authentication) {
    	
        return walletService.openWallet( openWalletRequest,authentication.getName());
        
    }

	/**
	 * Request fetch all wallet of an account holder
	 *
	 * @param holderId     - the user identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
    //@GetMapping("/ledger/{holderId}/wallets")
   // @Operation(summary = "List a holder's wallets, with cached and ledger balances")
    public List<WalletResponse> list(@PathVariable final String holderId,
    		                         final Authentication authentication) {
    	
        //return walletService.listWallets(holderId.toString(),authentication.getName());
    	return null;
    }

	/**
	 * Request fetch a wallet details based on wallet id
	 *
	 * @param walletId     - the wallet identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
  // @GetMapping("/wallets/{walletId}")
   //@Operation(summary = "One wallet, with its reconciliation status")
    public WalletResponse get(@PathVariable  final UUID walletId,
    		                 final Authentication authentication) { 
    	
        //return walletService.getWallet(walletId.toString(),authentication.getName());
    	return null;
    }
}
