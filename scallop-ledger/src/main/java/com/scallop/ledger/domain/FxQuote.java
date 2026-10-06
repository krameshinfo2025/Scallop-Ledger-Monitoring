package com.scallop.ledger.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A persisted, time-boxed FX rate that swap journals must reference. */
@Entity
@Table(name = "fx_quotes")
public class FxQuote {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "quote_id")
	private String quoteId;

	@Column(name = "entity_id", nullable = false)
	private String entityId;

	@Column(name = "base_currency", nullable = false, length = 16)
	private String baseCurrency;

	@Column(name = "target_currency", nullable = false, length = 16)
	private String targetCurrency;

	@Column(name = "rate", nullable = false, length = 255)
	private String rate;

	@Column(name = "observed_at", nullable = false)
	private Instant observedAt;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "source_reference", nullable = false)
	private String sourceReference;

	@Column(name = "created_by", nullable = false)
	private String createdBy;

	protected FxQuote() {
	}

	public FxQuote(UUID id, String quoteId, String entityId, String baseCurrency, String targetCurrency, String rate,
			Instant observedAt, Instant expiresAt, String sourceReference, String createdBy) {
		this.id = id;
		this.quoteId = quoteId;
		this.entityId = entityId;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
		this.observedAt = observedAt;
		this.expiresAt = expiresAt;
		this.sourceReference = sourceReference;
		this.createdBy = createdBy;
	}
	
	

	/**
	 * @param quoteId
	 * @param entityId
	 * @param baseCurrency
	 * @param targetCurrency
	 * @param rate
	 * @param observedAt
	 * @param expiresAt
	 * @param sourceReference
	 * @param createdBy
	 */
	public FxQuote(String quoteId, String entityId, String baseCurrency, String targetCurrency, String rate,
			Instant observedAt, Instant expiresAt, String sourceReference, String createdBy) {
		super();
		this.quoteId = quoteId;
		this.entityId = entityId;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
		this.observedAt = observedAt;
		this.expiresAt = expiresAt;
		this.sourceReference = sourceReference;
		this.createdBy = createdBy;
	}

	public UUID getId() {
		return id;
	}

	public String getQuoteId() {
		return quoteId;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public String getTargetCurrency() {
		return targetCurrency;
	}

	public String getRate() {
		return rate;
	}

	public Instant getObservedAt() {
		return observedAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public String getSourceReference() {
		return sourceReference;
	}

	public String getCreatedBy() {
		return createdBy;
	}
}
