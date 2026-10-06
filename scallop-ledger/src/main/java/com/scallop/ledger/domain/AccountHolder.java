/**
 ***********************************************************************************
 AccountHolder.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.AcountHolderAccountType;
import com.scallop.ledger.constant.HolderStatus;
import com.scallop.ledger.constant.HolderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * The party that owns wallets and that the bank owes money to. The ledger
 * records the holder; the audit trail records which {@link AppUser} acted.
 */
@Entity
@Table(name = "account_holder")
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "entity_id", nullable = false, updatable = false)
    private String entityId;
    
    @Column(name = "customer_id", nullable = false, updatable = false)
    private String customerId;
    
    @Column(name = "account_number", nullable = false, updatable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "holder_type", nullable = false, length = 16)
    private HolderType holderType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 16)
    private AcountHolderAccountType accountType = AcountHolderAccountType.MULTICURRENCY;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    /** Required for a business, forbidden for an individual - enforced by a check constraint. */
    @Column(name = "business_reg_no")
    private String businessRegNo;
    
    /** Required for a business, forbidden for an individual - enforced by a check constraint. */
    @Column(name = "role")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private HolderStatus status = HolderStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    
    @Column(name = "updated_by", nullable = false, updatable = true)
    private String updatedBy;

    protected AccountHolder() {
        // for JPA
    }


    
    /**
	 * @param id
	 * @param entityId
	 * @param customerId
	 * @param accountNumber
	 * @param holderType
	 * @param accountType
	 * @param displayName
	 * @param businessRegNo
	 * @param role
	 * @param status
	 */
	public AccountHolder(String entityId, String customerId, String accountNumber, HolderType holderType,
			AcountHolderAccountType accountType, String displayName, String businessRegNo, String role,
			HolderStatus status) {
		super();
		this.entityId = entityId;
		this.customerId = customerId;
		this.accountNumber = accountNumber;
		this.holderType = holderType;
		this.accountType = accountType;
		this.displayName = displayName;
		this.businessRegNo = businessRegNo;
		this.role = role;
		this.status = status;
	}



	/**
	 * @param entityId
	 * @param customerId
	 * @param accountNumber
	 * @param holderType
	 * @param accountType
	 * @param displayName
	 * @param businessRegNo
	 * @param role
	 * @param status
	 * @param createdAt
	 * @param updatedAt
	 * @param createdBy
	 * @param updatedBy
	 * @param version
	 */
	public AccountHolder(String entityId, String customerId, String accountNumber, HolderType holderType,
			AcountHolderAccountType accountType, String displayName, String businessRegNo, String role,
			HolderStatus status, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
		super();
		this.entityId = entityId;
		this.customerId = customerId;
		this.accountNumber = accountNumber;
		this.holderType = holderType;
		this.accountType = accountType;
		this.displayName = displayName;
		this.businessRegNo = businessRegNo;
		this.role = role;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

    @PrePersist
    void onInsert() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public HolderType getHolderType() {
        return holderType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBusinessRegNo() {
        return businessRegNo;
    }

    public HolderStatus getStatus() {
        return status;
    }

    public void setStatus(HolderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }



	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}



	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}



	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}



	/**
	 * @return the accountType
	 */
	public AcountHolderAccountType getAccountType() {
		return accountType;
	}



	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}



	/**
	 * @return the updatedAt
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}



	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}



	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}


}
