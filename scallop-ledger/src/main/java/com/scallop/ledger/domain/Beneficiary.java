/**
 ***********************************************************************************
 Beneficiary.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.BeneficiaryStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * An external party money can be paid out to: who they are
 * ({@link BeneficiaryAccountHolder}) and where they bank ({@link BeneficiaryBank}).
 * Never physically deleted - see {@link BeneficiaryStatus#DELETED}.
 */
@Entity
@Table(name = "beneficiary")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "beneficiary_type", nullable = false, length = 32)
    private String beneficiaryType;

    @Column(name = "is_third_party", nullable = false)
    private boolean thirdParty;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bank_id", nullable = false, unique = true)
    private BeneficiaryBank bank;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_holder_id", nullable = false, unique = true)
    private BeneficiaryAccountHolder accountHolder;

    /** Opaque client-supplied reference; not a foreign key. */
    @Column(name = "reference_id")
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BeneficiaryStatus status = BeneficiaryStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    /** Optimistic lock: two concurrent edits collide instead of the last one silently winning. */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected Beneficiary() {
        // for JPA
    }

    public Beneficiary(String createdBy) {
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
    }

    @PrePersist
    void onInsert() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void markDeleted(String actor) {
        this.status = BeneficiaryStatus.DELETED;
        this.updatedBy = actor;
    }

    public boolean isDeleted() {
        return status == BeneficiaryStatus.DELETED;
    }

    public UUID getId() {
        return id;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public boolean isThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(boolean thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BeneficiaryBank getBank() {
        return bank;
    }

    public void setBank(BeneficiaryBank bank) {
        this.bank = bank;
    }

    public BeneficiaryAccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(BeneficiaryAccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public BeneficiaryStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public long getVersion() {
        return version;
    }
}
