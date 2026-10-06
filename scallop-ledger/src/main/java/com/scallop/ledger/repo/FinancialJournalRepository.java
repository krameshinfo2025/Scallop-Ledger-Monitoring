/**
 ***********************************************************************************
 AppUserRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scallop.ledger.domain.FinancialJournal;
import com.scallop.ledger.response.LedgerRows.JournalRow;
import com.scallop.ledger.response.LedgerRows.OpenHold;

public interface FinancialJournalRepository extends JpaRepository<FinancialJournal, String> {

	@Query("SELECT new com.scallop.ledger.response.LedgerRows$JournalRow(j, c.eventKind)"
			+ " FROM FinancialJournal j JOIN JournalControl c ON c.journalId = j.journalId ORDER BY j.journalId")
	List<JournalRow> journals(Limit limit);

	/** HOLDs with no CAPTURE or RELEASE yet. */
	@Query("SELECT new com.scallop.ledger.response.LedgerRows$OpenHold(j.journalId, j.userId, j.baseCurrency,"
			+ " j.grossAmount, j.timestamp) FROM FinancialJournal j JOIN JournalControl c ON c.journalId = j.journalId"
			+ " WHERE c.eventKind = 'HOLD' AND NOT EXISTS (SELECT 1 FROM JournalControl x"
			+ " WHERE x.originalJournalId = j.journalId AND x.eventKind IN ('CAPTURE', 'RELEASE'))"
			+ " ORDER BY j.timestamp")
	List<OpenHold> openHolds(Limit limit);
}