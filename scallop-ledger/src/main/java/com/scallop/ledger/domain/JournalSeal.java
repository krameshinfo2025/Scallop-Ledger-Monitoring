/**
 ***********************************************************************************
 JournalSeal.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.time.Instant;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Canonical payload and SHA-256 that seal a journal (journal_seals). */
@Entity
@Immutable
@Table(name = "journal_seals")
public class JournalSeal {

	@Id
	@Column(name = "journal_id", length = 255)
	private String journalId;

	@Column(name = "canonical_payload", nullable = false)
	private String canonicalPayload;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "sha256", nullable = false, length = 64)
	private String sha256;

	/** Database default. */
	@Column(name = "sealed_at", insertable = false, updatable = false)
	private Instant sealedAt;

	protected JournalSeal() {
	}

	public JournalSeal(String journalId, String canonicalPayload, String sha256) {
		this.journalId = journalId;
		this.canonicalPayload = canonicalPayload;
		this.sha256 = sha256;
	}

	public String getId() {
		return journalId;
	}

	public String getCanonicalPayload() {
		return canonicalPayload;
	}

	public String getSha256() {
		return sha256;
	}

	public Instant getSealedAt() {
		return sealedAt;
	}
}
