/**
 ***********************************************************************************
 FinancialJournal.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Frozen journal header (V1). Column types and precision are fixed by the frozen schema. */
@Entity
@Immutable
@Table(name = "financial_journals")
public class FinancialJournal {

	@Id
	@Column(name = "journal_id", length = 255)
	@JsonProperty("journal_id")
	private String journalId;

	@Column(name = "timestamp", nullable = false)
	private Instant timestamp;

	@Column(name = "user_id", nullable = false, length = 255)
	@JsonProperty("user_id")
	private String userId;

	@Column(name = "tier_profile", nullable = false)
	@JsonProperty("tier_profile")
	private String tierProfile;

	@Column(name = "transaction_type", nullable = false, length = 64)
	@JsonProperty("transaction_type")
	private String transactionType;

	@Column(name = "base_currency", nullable = false, length = 16)
	@JsonProperty("base_currency")
	private String baseCurrency;

	@Column(name = "network", length = 32)
	private String network;

	@Column(name = "target_currency", length = 16)
	@JsonProperty("target_currency")
	private String targetCurrency;

	@Column(name = "gross_amount", nullable = false, precision = 18, scale = 6)
	@JsonProperty("gross_amount")
	private BigDecimal grossAmount;

	@Column(name = "fee_breakdown", nullable = false)
	@JsonProperty("fee_breakdown")
	private String feeBreakdown;

	@Column(name = "net_processed_amount", nullable = false, precision = 18, scale = 6)
	@JsonProperty("net_processed_amount")
	private BigDecimal netProcessedAmount;

	@Column(name = "oracle_spot_rate", precision = 18, scale = 6)
	@JsonProperty("oracle_spot_rate")
	private BigDecimal oracleSpotRate;

	@Column(name = "source_rail_address", length = 255)
	@JsonProperty("source_rail_address")
	private String sourceRailAddress;

	@Column(name = "counterparty_endpoint", length = 255)
	@JsonProperty("counterparty_endpoint")
	private String counterpartyEndpoint;

	@Column(name = "created_at")
	@JsonProperty("created_at")
	private Instant createdAt;

	protected FinancialJournal() {
	}

	public FinancialJournal(String journalId, Instant timestamp, String userId, String tierProfile,
			String transactionType, String baseCurrency, String network, String targetCurrency,
			BigDecimal grossAmount, String feeBreakdown, BigDecimal netProcessedAmount, BigDecimal oracleSpotRate,
			String sourceRailAddress, String counterpartyEndpoint, Instant createdAt) {
		this.journalId = journalId;
		this.timestamp = timestamp;
		this.userId = userId;
		this.tierProfile = tierProfile;
		this.transactionType = transactionType;
		this.baseCurrency = baseCurrency;
		this.network = network;
		this.targetCurrency = targetCurrency;
		this.grossAmount = grossAmount;
		this.feeBreakdown = feeBreakdown;
		this.netProcessedAmount = netProcessedAmount;
		this.oracleSpotRate = oracleSpotRate;
		this.sourceRailAddress = sourceRailAddress;
		this.counterpartyEndpoint = counterpartyEndpoint;
		this.createdAt = createdAt;
	}

	public String getId() {
		return journalId;
	}

	public String getJournalId() {
		return journalId;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String getUserId() {
		return userId;
	}

	public String getTierProfile() {
		return tierProfile;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public String getNetwork() {
		return network;
	}

	public String getTargetCurrency() {
		return targetCurrency;
	}

	public BigDecimal getGrossAmount() {
		return grossAmount;
	}

	public String getFeeBreakdown() {
		return feeBreakdown;
	}

	public BigDecimal getNetProcessedAmount() {
		return netProcessedAmount;
	}

	public BigDecimal getOracleSpotRate() {
		return oracleSpotRate;
	}

	public String getSourceRailAddress() {
		return sourceRailAddress;
	}

	public String getCounterpartyEndpoint() {
		return counterpartyEndpoint;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
