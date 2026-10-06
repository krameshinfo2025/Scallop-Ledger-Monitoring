package com.scallop.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.AssetKind;
import com.scallop.ledger.constant.WalletStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * A holder's balance in one currency.
 *
 * <p>{@code balance} is a cached projection of the journal, not the truth. It
 * exists so a balance check does not have to aggregate the whole ledger on
 * every request. Reconciliation recomputes it from journal lines and proves
 * the two agree.
 */
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "holder_id", nullable = false, updatable = false)
    private UUID holder;

    @Column(name = "currency_code", nullable = false, updatable = false)
    private String currency;
    
	/** Mirrors currency_scale_range: 0..18 */
	@NotNull
	@Min(0)
	@Max(18)
	@Column(name = "display_scale", nullable = false)
	private Short displayScale;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "kind", length = 10, nullable = false)
	private AssetKind kind;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(name = "balance", nullable = false, precision = 38, scale = 18)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    
    @Column(name = "updated_by", nullable = false, updatable = true)
    private String updatedBy;

    /**
     * Optimistic lock. Two concurrent debits on this wallet both read the same
     * version; the second to flush fails rather than overwriting the first.
     * This is what stops a double-spend from interleaved reads.
     */
    @Version
    @Column(name = "version", nullable = false,updatable = false)
    private Short version;


    protected Wallet() {
        // for JPA
    }

 
	/**
	 * @param id
	 * @param holder
	 * @param currency
	 * @param displayScale
	 * @param kind
	 * @param status
	 * @param balance
	 */
	public Wallet(UUID holder, String currency, Short displayScale,
			AssetKind kind, WalletStatus status, BigDecimal balance) {
		super();
		this.holder = holder;
		this.currency = currency;
		this.displayScale = displayScale;
		this.kind = kind;
		this.status = status;
		this.balance = balance;
	}
	
	


	/**
	 * @param holder
	 * @param currency
	 * @param displayScale
	 * @param kind
	 * @param status
	 * @param balance
	 * @param createdAt
	 * @param updatedAt
	 * @param createdBy
	 * @param updatedBy
	 */
	public Wallet(UUID holder, String currency, @NotNull @Min(0) @Max(18) Short displayScale, @NotNull AssetKind kind,
			WalletStatus status, BigDecimal balance, Instant createdAt, Instant updatedAt, String createdBy,
			String updatedBy) {
		super();
		this.holder = holder;
		this.currency = currency;
		this.displayScale = displayScale;
		this.kind = kind;
		this.status = status;
		this.balance = balance;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}


	@PrePersist
    void onInsert() {
        this.createdAt = Instant.now();
    }

    public boolean isFrozen() {
        return status == WalletStatus.FROZEN;
    }

    public boolean isClosed() {
        return status == WalletStatus.CLOSED;
    }

    public UUID getId() {
        return id;
    }


	/**
	 * @return the holder
	 */
	public UUID getHolder() {
		return holder;
	}


	/**
	 * @param holder the holder to set
	 */
	public void setHolder(UUID holder) {
		this.holder = holder;
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
	 * @return the status
	 */
	public WalletStatus getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(WalletStatus status) {
		this.status = status;
	}


	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}


	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
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


	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}


}
