package com.scallop.ledger.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scallop.ledger.domain.FinancialJournal;
import com.scallop.ledger.domain.JournalControl;


/**
 * Read models built by JPQL constructor expressions. JSON keys stay snake_case
 * because the console (static/app.js) reads the column names the JDBC
 * implementation used to return.
 */
public final class LedgerRows {

	private LedgerRows() {
	}

	/** A journal header and its control row; internal, never serialized. */
	public record JournalHeader(FinancialJournal journal, JournalControl control) {
	}

	public record JournalRow(@JsonUnwrapped FinancialJournal journal,
			@JsonProperty("event_kind") String eventKind) {
	}

	public record OpenHold(@JsonProperty("journal_id") String journalId, @JsonProperty("user_id") String userId,
			@JsonProperty("base_currency") String baseCurrency,
			@JsonProperty("reserved_amount") @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal reservedAmount,
			Instant timestamp) {
	}

	public record QuoteRow(@JsonProperty("quote_id") String quoteId,
			@JsonProperty("base_currency") String baseCurrency,
			@JsonProperty("target_currency") String targetCurrency,
			@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal rate,
			@JsonProperty("observed_at") Instant observedAt, @JsonProperty("expires_at") Instant expiresAt,
			@JsonProperty("source_reference") String sourceReference) {
	}
}
