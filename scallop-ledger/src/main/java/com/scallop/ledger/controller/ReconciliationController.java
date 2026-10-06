/**
 ***********************************************************************************
 ReconciliationController.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.domain.ReserveObservation;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.Observation;
import com.scallop.ledger.response.ResponseData.ReconciliationResponse;
import com.scallop.ledger.service.ReconciliationService;

/**
 * Controller for managing Reconciliation operations. 
 * This includes retrieving Reconciliation information, and add Reconciliation information.
 * 
 * @author Ramesh Kaliindi
 */
@RestController
@RequestMapping("/api/v1")
public class ReconciliationController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for reconciliationService of type  ReconciliationService */
	private final ReconciliationService reconciliationService;

	/**
	 * ReconciliationController Constructor 
	 * 
	 * @param reconciliationService
	 */
	public ReconciliationController(final ReconciliationService reconciliationService) {
		this.reconciliationService = reconciliationService;
	}

	/**
	 * Method for fetch all Reconciliations.
	 *
 	 * @return data containing the Reconciliations from the service layer.
	 * @throws LedgerException if any 
	 */
	@GetMapping("/reconciliation")
	public List<ReserveObservation> reconciliation() throws LedgerException{
		
		// fetch the Reconciliations from the service layer by invoking getReconciliations on reconciliationService
		List<ReserveObservation> data = reconciliationService.getReconciliations();
		
		return data;
	}
	
	/**
	 * Method for save Reconciliation details in the System.
	 * 
	 * @param quote - user input for Reconciliations of type RequestData.Observation.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any 
	 */
	@PostMapping("/reconciliation")
	public ReconciliationResponse reconcile(@RequestBody Observation reconcillation, Authentication authentication) throws LedgerException{
		
		// save the Quotes by invoking saveQuote on quotesService
		ReconciliationResponse data = reconciliationService.saveReconcile(reconcillation, authentication.getName());
		
		return data;
		
	}


}
