/**
 ***********************************************************************************
 JournalControl.Java
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One double-entry leg (journal_double_entry_postings). */
@Entity
@Immutable
@Table(name = "journal_double_entry_postings")
public class JournalPosting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "journal_id", length = 255)
	private String journalId;

	@Column(name = "account", nullable = false, length = 255)
	private String account;

	@Column(name = "currency", nullable = false, length = 16)
	private String currency;

	@Column(name = "posting_type", nullable = false, length = 2)
	private String postingType;

	@Column(name = "amount", nullable = false, precision = 18, scale = 6)
	private BigDecimal amount;

	@Column(name = "description")
	private String description;

	@Column(name = "posting_time", nullable = false)
	private Instant postingTime;

	protected JournalPosting() {
	}

	public JournalPosting(String journalId, String account, String currency, String postingType, BigDecimal amount,
			String description, Instant postingTime) {
		this.journalId = journalId;
		this.account = account;
		this.currency = currency;
		this.postingType = postingType;
		this.amount = amount;
		this.description = description;
		this.postingTime = postingTime;
	}

	public Long getId() {
		return id;
	}

	public String getJournalId() {
		return journalId;
	}

	public String getAccount() {
		return account;
	}

	public String getCurrency() {
		return currency;
	}

	public String getPostingType() {
		return postingType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Instant getPostingTime() {
		return postingTime;
	}
}
