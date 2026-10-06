/**
 * 
 */
package com.scallop.ledger.repo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scallop.ledger.domain.FinancialJournal;

/**
 * 
 */
public interface LedgerRepository extends JpaRepository<FinancialJournal, String> {

	@Query("SELECT new com.scallop.ledger.response.LedgerRows$OpenHold(j.journalId, j.userId, j.baseCurrency,"
			+ " j.grossAmount, j.timestamp) FROM FinancialJournal j JOIN JournalControl c ON c.journalId = j.journalId"
			+ " WHERE c.eventKind = 'HOLD' AND NOT EXISTS (SELECT 1 FROM JournalControl x"
			+ " WHERE x.originalJournalId = j.journalId AND x.eventKind IN ('CAPTURE', 'RELEASE'))"
			+ " ORDER BY j.timestamp")
	List<Map<String, Object>> historicalBalances(Instant time, Object object, Object object2, Object object3);

	@Query(value = "SELECT j.*,c.event_kind,c.original_journal_id,c.actor,c.deterministic_id,c.entity_id "
			+ "FROM financial_journals j JOIN journal_control c ON j.journal_id = c.journal_id "
			+ "WHERE j.journal_id=:id",nativeQuery = true)
	Map<String, Object> trace(@Param("id") String id);
	
	
	@Query(value = "SELECT COALESCE(SUM(CASE WHEN p.posting_type='CR' THEN p.amount ELSE -p.amount END),0) "
			+ "FROM journal_double_entry_postings p JOIN journal_control c ON p.journal_id = c.journal_id  "
			+ "WHERE c.entity_id=:entity AND p.account=:account "
			+ "AND p.currency=:currency AND p.posting_time<=:at",nativeQuery = true)
	BigDecimal balance(@Param("entity") String entity,@Param("account") String account, 
			@Param("currency") String currency, @Param("at") Instant at);

}
