/**
 ***********************************************************************************
 ReconciliationService.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import java.util.List;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.Observation;
import com.scallop.ledger.response.ResponseData.ReconciliationResponse;

/**
 * QuotesService is an interface for fetch Reconciliations 
 * of all the users.
 * 
 */
public interface ReconciliationService {

	

	/**
	 * Method for fetch all Reconciliations.
	 *
 	 * @return data containing the Reconciliations from the service layer.
	 * @throws LedgerException if any 
	 */
	public List<com.scallop.ledger.domain.ReserveObservation> getReconciliations();

	/**
	 * Method for save Reconciliation details in the System.
	 * 
	 * @param quote - user input for Reconciliations of type RequestData.Observation.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the Reconciliation from the service layer.
	 * @throws LedgerException if any 
	 */
	public ReconciliationResponse saveReconcile(Observation reconcillation, String actor);

	
	

	

}
