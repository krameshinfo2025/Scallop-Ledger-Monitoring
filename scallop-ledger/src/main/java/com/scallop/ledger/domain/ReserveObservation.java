package com.scallop.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** An external statement balance compared with the ledger at the same instant. */
@Entity
@Immutable
@Table(name = "reserve_observations")
public class ReserveObservation {

	@Id
	@Column(name = "observation_id")
	@JsonProperty("observation_id")
	private UUID observationId;

	@Column(name = "entity_id", nullable = false)
	@JsonProperty("entity_id")
	private String entityId;

	@Column(name = "account", nullable = false, length = 255)
	private String account;

	@Column(name = "currency", nullable = false, length = 16)
	private String currency;

	@Column(name = "statement_reference", nullable = false)
	@JsonProperty("statement_reference")
	private String statementReference;

	@Column(name = "observed_at", nullable = false)
	@JsonProperty("observed_at")
	private Instant observedAt;

	@Column(name = "external_balance", nullable = false, precision = 18, scale = 6)
	@JsonProperty("external_balance")
	private BigDecimal externalBalance;

	@Column(name = "ledger_balance", nullable = false, precision = 18, scale = 6)
	@JsonProperty("ledger_balance")
	private BigDecimal ledgerBalance;

	@Column(name = "difference", nullable = false, precision = 18, scale = 6)
	private BigDecimal difference;

	@Column(name = "recorded_by", nullable = false)
	@JsonProperty("recorded_by")
	private String recordedBy;

	protected ReserveObservation() {
	}
	
	/**
	 * @param entityId
	 * @param account
	 * @param currency
	 * @param statementReference
	 * @param observedAt
	 * @param externalBalance
	 * @param ledgerBalance
	 * @param difference
	 * @param recordedBy
	 */
	public ReserveObservation(String entityId, String account, String currency, String statementReference,
			Instant observedAt, BigDecimal externalBalance, BigDecimal ledgerBalance, BigDecimal difference,
			String recordedBy) {
		super();
		this.entityId = entityId;
		this.account = account;
		this.currency = currency;
		this.statementReference = statementReference;
		this.observedAt = observedAt;
		this.externalBalance = externalBalance;
		this.ledgerBalance = ledgerBalance;
		this.difference = difference;
		this.recordedBy = recordedBy;
	}



	public ReserveObservation(UUID observationId, String entityId, String account, String currency,
			String statementReference, Instant observedAt, BigDecimal externalBalance, BigDecimal ledgerBalance,
			BigDecimal difference, String recordedBy) {
		this.observationId = observationId;
		this.entityId = entityId;
		this.account = account;
		this.currency = currency;
		this.statementReference = statementReference;
		this.observedAt = observedAt;
		this.externalBalance = externalBalance;
		this.ledgerBalance = ledgerBalance;
		this.difference = difference;
		this.recordedBy = recordedBy;
	}

	public UUID getId() {
		return observationId;
	}

	public UUID getObservationId() {
		return observationId;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getAccount() {
		return account;
	}

	public String getCurrency() {
		return currency;
	}

	public String getStatementReference() {
		return statementReference;
	}

	public Instant getObservedAt() {
		return observedAt;
	}

	public BigDecimal getExternalBalance() {
		return externalBalance;
	}

	public BigDecimal getLedgerBalance() {
		return ledgerBalance;
	}

	public BigDecimal getDifference() {
		return difference;
	}

	public String getRecordedBy() {
		return recordedBy;
	}
}
