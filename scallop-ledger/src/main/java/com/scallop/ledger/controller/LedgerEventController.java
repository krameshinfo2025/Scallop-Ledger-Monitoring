/**
 * 
 */
package com.scallop.ledger.controller;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.LedgerEventListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEventResponse;
import com.scallop.ledger.service.LedgerEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * an auto-complete endpoint to filter Ledger Event as the Transaction types, 
 * and a paginated endpoint to return data filtered by the selected EventId
 */
@RestController
@RequestMapping("/api/v1/ledger-Event")
@Tag(name = "Ledger Event Management")
public class LedgerEventController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	/** The Place Holder for ledgerEventService of type LedgerEntitiyService */
	private final LedgerEventService ledgerEventService;

    
  	/**
	 * @param ledgerEventService
	 */
	public LedgerEventController(LedgerEventService ledgerEventService) {
		super();
		this.ledgerEventService = ledgerEventService;
	}


	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the LedgerEventResponse List of type PagedResponse.LedgerEventResponse.
  	 * @throws LedgerException if any 
  	 */
      @GetMapping("/getall")
      @Operation(summary = "List all Ledger Entities")
      public PagedResponse<LedgerEventResponse> getAllLedgerEvents( 
     		            @RequestParam(defaultValue = "0") int page,
     		            @RequestParam(defaultValue = "5") int size,
     		            @RequestParam(defaultValue = "id") String sortBy,
     		            @RequestParam(defaultValue = "true") boolean ascending,
     		            @Valid @RequestBody LedgerEventListRequest ledgerEventListRequest,
     		            final Authentication authentication) {
     	 
     	 Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
          Pageable pageable = PageRequest.of(page, size, sort);
     	 
          return ledgerEventService.getAllLedgerEvents(pageable,ledgerEventListRequest,authentication.getName());
          
      }

}
