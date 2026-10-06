/**
 ***********************************************************************************
 HistoricalJournalBalanceServiceImpl.Java
 version 1.0
 24,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */

package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.LedgerRepository;
import com.scallop.ledger.service.HistoricalJournalBalanceService;
import com.scallop.ledger.service.helper.JournalFactory;

/**
 * HistoricalJournalBalanceServiceImpl is a class for fetch Historical balance
 * of all the users or a particular user based on user identifier.
 *
 */
@Service(value = "historicalJournalBalanceService")
public class HistoricalJournalBalanceServiceImpl implements HistoricalJournalBalanceService,Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for ledgerRepository of type  LedgerRepository */
	private final LedgerRepository ledgerRepository;

	/**
	 * @param ledgerRepository
	 */
	public HistoricalJournalBalanceServiceImpl(final LedgerRepository ledgerRepository) {
		this.ledgerRepository = ledgerRepository;
	}

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
	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getHistoricalJournalBalances(final Instant time, final String userId)
			throws LedgerException {

		// with a user identifier, only that user's customer and card accounts are summed
		if (userId != null) {
			return ledgerRepository.historicalBalances(time, userId, JournalFactory.user(userId),
					JournalFactory.card(userId));
		}

		return ledgerRepository.historicalBalances(time, null, null, null);
	}

}
