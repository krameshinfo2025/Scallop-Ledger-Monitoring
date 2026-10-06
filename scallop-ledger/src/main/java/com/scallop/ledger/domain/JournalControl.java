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

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entity scoping, idempotency and lifecycle linkage for one journal. */
@Entity
@Immutable
@Table(name = "journal_control")
public class JournalControl {

	@Id
	@Column(name = "journal_id", length = 255)
	private String journalId;

	@Column(name = "entity_id", nullable = false)
	private String entityId;

	@Column(name = "actor", nullable = false)
	private String actor;

	@Column(name = "request_key", nullable = false)
	private String requestKey;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "deterministic_id", nullable = false, length = 32)
	private String deterministicId;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "request_hash", nullable = false, length = 64)
	private String requestHash;

	@Column(name = "original_journal_id", length = 255)
	private String originalJournalId;

	@Column(name = "event_kind", nullable = false)
	private String eventKind;

	protected JournalControl() {
	}

	public JournalControl(String journalId, String entityId, String actor, String requestKey, String deterministicId,
			String requestHash, String originalJournalId, String eventKind) {
		this.journalId = journalId;
		this.entityId = entityId;
		this.actor = actor;
		this.requestKey = requestKey;
		this.deterministicId = deterministicId;
		this.requestHash = requestHash;
		this.originalJournalId = originalJournalId;
		this.eventKind = eventKind;
	}


	public String getId() {
		return journalId;
	}

	public String getJournalId() {
		return journalId;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getActor() {
		return actor;
	}

	public String getRequestKey() {
		return requestKey;
	}

	public String getDeterministicId() {
		return deterministicId;
	}

	public String getRequestHash() {
		return requestHash;
	}

	public String getOriginalJournalId() {
		return originalJournalId;
	}

	public String getEventKind() {
		return eventKind;
	}
}
