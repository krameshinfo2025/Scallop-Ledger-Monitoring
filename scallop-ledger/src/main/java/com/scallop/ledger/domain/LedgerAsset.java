/**
 ***********************************************************************************
 LedgerAsset.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.scallop.ledger.constant.AssetKind;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ledger_assets")
public class LedgerAsset {

	public static final String SYSTEM_USER = "SCALLOP_SL";

	/** Primary key. Mirrors currency_code_format: ^[A-Z]{3,10}$ */
	@Id
	@NotBlank
	@Pattern(regexp = "^[A-Z]{3,10}$")
	@Column(name = "currency", length = 16, nullable = false, updatable = false)
	private String currency;

	/** Mirrors currency_scale_range: 0..18 */
	@NotNull
	@Min(0)
	@Max(18)
	@Column(name = "display_scale", nullable = false)
	private Short displayScale;

	@NotBlank
	@Column(name = "display_name", nullable = false, columnDefinition = "text")
	private String displayName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "kind", length = 10, nullable = false)
	private AssetKind kind;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active = Boolean.TRUE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz")
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
	private Instant updatedAt;

	@NotBlank
	@Size(max = 36)
	@Column(name = "created_by", length = 36, nullable = false, updatable = false)
	private String createdBy = SYSTEM_USER;

	@NotBlank
	@Size(max = 36)
	@Column(name = "updated_by", length = 36, nullable = false)
	private String updatedBy = SYSTEM_USER;

	/**
	 * Optimistic-lock version. Left null on new instances so Spring Data treats the
	 * entity as new (the ID is assigned, not generated).
	 */
	@Version
	@Column(name = "version", nullable = false,updatable = false)
	private Short version;

	/**
	 * 
	 */
	public LedgerAsset() {
		super();
	}

	public LedgerAsset(String currency, short displayScale, String displayName, AssetKind kind) {
		this.currency = currency;
		this.displayScale = displayScale;
		this.displayName = displayName;
		this.kind = kind;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LedgerAsset other))
			return false;
		return currency != null && currency.equals(other.currency);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(currency);
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the displayScale
	 */
	public Short getDisplayScale() {
		return displayScale;
	}

	/**
	 * @param displayScale the displayScale to set
	 */
	public void setDisplayScale(Short displayScale) {
		this.displayScale = displayScale;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the kind
	 */
	public AssetKind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(AssetKind kind) {
		this.kind = kind;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the createdAt
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the version
	 */
	public Short getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Short version) {
		this.version = version;
	}

}
