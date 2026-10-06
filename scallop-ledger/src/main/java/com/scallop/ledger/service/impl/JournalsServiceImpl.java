/**
 ***********************************************************************************
 JournalsServiceImpl.Java
 version 1.0
 24,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.FinancialJournalRepository;
import com.scallop.ledger.repo.LedgerRepository;
import com.scallop.ledger.response.LedgerRows.JournalRow;
import com.scallop.ledger.response.LedgerRows.OpenHold;
import com.scallop.ledger.service.JournalsService;

/**
 * JournalsServiceImpl is a Class for fetch Journals and Open hold Journals
 * of all the users.
 *
 */
@Service(value = "journalsService")
public class JournalsServiceImpl implements JournalsService,Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for ledgerRepository of type  LedgerRepository */
	private final LedgerRepository ledgerRepository;

	/** The Place Holder for financialJournalRepository of type  FinancialJournalRepository */
	private final FinancialJournalRepository financialJournalRepository;

	/**
	 * @param ledgerRepository
	 * @param financialJournalRepository
	 */
	public JournalsServiceImpl(final LedgerRepository ledgerRepository,
			final FinancialJournalRepository financialJournalRepository) {
		this.ledgerRepository = ledgerRepository;
		this.financialJournalRepository = financialJournalRepository;
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
	@Override
	@Transactional(readOnly = true)
	public List<JournalRow> getJournals(final int limit, final String after) throws LedgerException{

		// fetch Journals with their event kind, ordered by journal identifier
		return financialJournalRepository.journals(Limit.of(limit));
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
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getTrace(final String journalId) throws LedgerException{

		// fetch Journal detail from db using Repository layer by invoking trace on ledgerRepository
		Map<String, Object> data = ledgerRepository.trace(journalId);

		return data;
	}


	@Override
	@Transactional(readOnly = true)
	public List<OpenHold> getOPenHolds() throws LedgerException{

		// fetch the oldest HOLDs that have not been captured or released
		return financialJournalRepository.openHolds(Limit.of(100));

	}

}
