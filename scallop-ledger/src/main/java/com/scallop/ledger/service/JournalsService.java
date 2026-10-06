/**
 ***********************************************************************************
 JournalsService.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import java.util.List;
import java.util.Map;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.response.LedgerRows.JournalRow;
import com.scallop.ledger.response.LedgerRows.OpenHold;

/**
 * JournalsService is an interface for fetch Journals and Open hold Journals
 * of all the users.
 * 
 */
public interface JournalsService {

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
	List<JournalRow> getJournals(final int limit, final String after)throws LedgerException;

	/**
	 * Method for fetch all Journal details by User Identifier.
	 *
	 * @param journalId - the  Journal identifier passed in the request header.
	 * 
	 *                       
	 * @return data containing the Journal details from the service layer.
	 * @throws LedgerException if any 
	 */
	Map<String, Object> getTrace(final String journalId)throws LedgerException;

	/**
	 * Method for fetch all OPen Hold Journals
	 *
	 * @return data containing the OPen Hold Journals from the service layer.
	 * @throws LedgerException if any 
	 */
	List<OpenHold> getOPenHolds()throws LedgerException;

}
