/**
 ***********************************************************************************
 HistoricalJournalBalanceController.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.service.HistoricalJournalBalanceService;

/**
 *  Controller for managing Historical balance operations and 
 *  an entry class for fetch Historical balance of all the users or
 *  a particular user based on user identifier.
 * 
 *  @author Ramesh Kaliindi
 */
@RestController
@RequestMapping("/api")
public class HistoricalJournalBalanceController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for historicalJournalBalanceService of type  HistoricalJournalBalanceService */
	private final HistoricalJournalBalanceService historicalJournalBalanceService;

	/**
	 * HistoricalJournalBalanceController Constructor 
	 * 
	 * @param historicalJournalBalanceService
	 * 
	 */
	public HistoricalJournalBalanceController(final HistoricalJournalBalanceService historicalJournalBalanceService) {
		
		this.historicalJournalBalanceService = historicalJournalBalanceService;

	}

	/**
	 * Method for fetch a Historical Balance to user(s).
	 *
	 * @param at - the time passed in the request header.
	 * @param userId - the user identifier passed in the request header.
	 * 
	 *                         
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any 
	 */
	@GetMapping("/balances")
	public Object balances(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant at, @RequestParam(required = false) String userId) {
		
		// the time passed in the request header is null set current time as at
		Instant time = at == null ? Instant.now() : at;
		
		//if the time passed in the request header is more than one second of the current time 
		// throw exception with message "Historical balance time cannot be in future"
		if (time.isAfter(Instant.now().plusSeconds(1)))
			throw LedgerException.invalid("Historical balance time cannot be in future");
		
		// fetch the Historical Balances from the service layer by invoking getHistoricalJournalBalances on historicalJournalBalanceService
		var data = historicalJournalBalanceService.getHistoricalJournalBalances(time,userId);
		
		return data;
	}

}
