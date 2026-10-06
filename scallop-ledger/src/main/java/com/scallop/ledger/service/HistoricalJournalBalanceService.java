/**
 ***********************************************************************************
 HistoricalJournalBalanceService.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.scallop.ledger.exception.LedgerException;

/**
 * HistoricalJournalBalanceService is an interface for fetch Historical balance
 * of all the users or a particular user based on user identifier.
 * 
 */
public interface HistoricalJournalBalanceService {

	/**
	 * Request fetch a Historical Balance to user(s).
	 *
	 * @param at     - the time passed in the request header.
	 * @param userId - the user identifier passed in the request header.
	 * 
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
	List<Map<String, Object>> getHistoricalJournalBalances(final Instant at, final String userId)
			throws LedgerException;

}
