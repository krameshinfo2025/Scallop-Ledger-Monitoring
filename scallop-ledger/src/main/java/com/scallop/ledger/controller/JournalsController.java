/**
 ***********************************************************************************
 JournalsController.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.service.JournalsService;

/**
 * Controller for managing Journals operations. 
 * This includes retrieving Journals information, and Journals Hold information.
 * 
 * @author Ramesh Kaliindi
 */
@RestController
@RequestMapping("/api/v1")
public class JournalsController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for journalsService of type  JournalsService */
	private final JournalsService journalsService;

	/**
	 * JournalsController Constructor 
	 * 
	 * @param journalsService
	 */
	public JournalsController(final JournalsService journalsService) {
		this.journalsService = journalsService;
	}
	
	/**
	 * Method for fetch all Journals.
	 *
	 * @param limit - the limit of records passed in the request header.
	 * @param after - the after the journal identifier passed in the request header.
	 * 
	 *                       
	 * @return data containing the Journals from the service layer.
	 * @throws LedgerException if any 
	 */
	@GetMapping("/journals")
	public Object journals( @RequestParam(defaultValue = "100") final int limit,
			                @RequestParam(defaultValue = "") final String after) throws LedgerException{
		
		// set the actualLimit 1 or minimum of limit and 100
		int actualLimit = Math.max(1, Math.min(limit, 100));
		
		// fetch the Journals from the service layer by invoking getJournals on journalsService
		var data = journalsService.getJournals(actualLimit,after);
				
		return data;
	}

	/**
	 * Method for fetch all Journal details by User Identifier.
	 *
	 * @param journalId - the  Journal identifier passed in the request header.
	 * 
	 *                       
	 * @return data containing the Journal details from the service layer.
	 * @throws LedgerException if any 
	 */
	@GetMapping("/journals/{id}")
	public Object trace(@PathVariable String id) throws LedgerException{
		
		// fetch the Journal detail from the service layer by invoking getTrace on journalsService
		Map<String, Object> data = journalsService.getTrace(id);
				
		return data;		
		
	}
	
	/**
	 * Method for fetch all OPen Hold Journals
	 *
	 * @return data containing the OPen Hold Journals from the service layer.
	 * @throws LedgerException if any 
	 */
	@GetMapping("/journals/holds")
	public Object holds() throws LedgerException{
		
		// fetch the OPen Hold Journals from the service layer by invoking getOPenHolds on journalsService
		var data = journalsService.getOPenHolds();
		
		return data;		
	}


	
	

}
