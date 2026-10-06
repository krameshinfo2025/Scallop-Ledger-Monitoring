/**
 ***********************************************************************************
 LedgerEntitiy.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * Registered (entity, account, asset). Rows are created with INSERT ... ON
 * CONFLICT DO NOTHING (see LedgerAccountRepository) and then row-locked, so
 * concurrent postings to one account serialize. Not {@code @Immutable} only
 * because Hibernate treats those as read-only and this row is locked; the
 * immutable_record trigger still rejects any UPDATE.
 */
@Entity
@IdClass(LedgerAccount.Key.class)
@Table(name = "ledger_accounts")
public class LedgerAccount {

	@Id
	@Column(name = "entity_id")
	private String entityId;

	@Id
	@Column(name = "account", length = 255)
	private String account;

	@Id
	@Column(name = "currency", length = 16)
	private String currency;

	@Column(name = "owner_id")
	private String ownerId;

	@Column(name = "rail_reference")
	private String railReference;

	protected LedgerAccount() {
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

	public String getOwnerId() {
		return ownerId;
	}

	public String getRailReference() {
		return railReference;
	}

	public static class Key implements Serializable {
		private static final long serialVersionUID = 1L;
		private String entityId;
		private String account;
		private String currency;

		protected Key() {
		}

		public Key(String entityId, String account, String currency) {
			this.entityId = entityId;
			this.account = account;
			this.currency = currency;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Key k && Objects.equals(entityId, k.entityId) && Objects.equals(account, k.account)
					&& Objects.equals(currency, k.currency);
		}

		@Override
		public int hashCode() {
			return Objects.hash(entityId, account, currency);
		}
	}
}
