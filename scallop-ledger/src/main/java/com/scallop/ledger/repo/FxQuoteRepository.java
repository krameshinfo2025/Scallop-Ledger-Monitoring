package com.scallop.ledger.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scallop.ledger.domain.FxQuote;

public interface FxQuoteRepository extends JpaRepository<FxQuote, String> {
	
	/** quote_id is not unique, so the most recently observed quote of that id wins. */
	Optional<FxQuote> findFirstByQuoteIdAndEntityIdOrderByObservedAtDesc(String quoteId, String entityId);


	/** A quote for this entity and pair that is already observed and not yet expired, on the database clock. 
	@Query("SELECT q FROM FxQuote q WHERE q.quoteId = :quoteId AND q.entityId = :entityId"
			+ " AND q.baseCurrency = :baseCurrency AND q.targetCurrency = :targetCurrency"
			+ " AND q.expiresAt > CURRENT_TIMESTAMP AND q.observedAt <= CURRENT_TIMESTAMP")
	Optional<FxQuote> findUsable(String quoteId, String entityId, String baseCurrency, String targetCurrency);
	

	@Query("SELECT new com.scallop.ledger.response.LedgerRows$QuoteRow(q.quoteId, q.baseCurrency,"
			+ " q.targetCurrency, q.rate, q.observedAt, q.expiresAt, q.sourceReference)"
			+ " FROM FxQuote q ORDER BY q.observedAt DESC")
	List<QuoteRow> latest(Limit limit);
	*/
}