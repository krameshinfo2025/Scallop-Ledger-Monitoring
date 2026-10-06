/**
 ***********************************************************************************
 Payment.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.PaymentStatus;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * A record of a payment from a {@link PaymentPayer} to a {@link PaymentPayee}.
 * Does not post to the ledger. Once settled or locked it can no longer be
 * changed, and it is never physically deleted - see {@link PaymentStatus#CANCELLED}.
 */
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Shared, independently owned quote: never cascaded. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fx_quote_id")
    private FxQuote fxQuote;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payer_id", nullable = false, unique = true)
    private PaymentPayer payer;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payee_id", nullable = false, unique = true)
    private PaymentPayee payee;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "amount_id", nullable = false, unique = true)
    private PaymentAmount amount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "additional_info_id", unique = true)
    private PaymentAdditionalInfo additionalInfo;

    /** PaymentTransaction.id as supplied by the client; not a key. */
    @Column(name = "external_id", length = 120)
    private String externalId;

    @Column(name = "transaction_id", length = 120)
    private String transactionId;

    @Column(name = "original_transaction_id", length = 120)
    private String originalTransactionId;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "transaction_type", nullable = false, length = 64)
    private String transactionType;

    @Column(name = "transaction_sub_type", length = 64)
    private String transactionSubType;

    @Column(name = "transfer_type", length = 64)
    private String transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "is_settled", nullable = false)
    private boolean settled;

    @Column(name = "is_locked", nullable = false)
    private boolean locked;

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

    protected Payment() {
        // for JPA
    }

    public Payment(String createdBy) {
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

    public void markCancelled(String actor) {
        this.status = PaymentStatus.CANCELLED;
        this.updatedBy = actor;
    }

    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }

    /** Settled, locked and cancelled payments are final. */
    public boolean isFrozen() {
        return settled || locked || isCancelled();
    }

    public UUID getId() {
        return id;
    }

    public FxQuote getFxQuote() {
        return fxQuote;
    }

    public void setFxQuote(FxQuote fxQuote) {
        this.fxQuote = fxQuote;
    }

    public PaymentPayer getPayer() {
        return payer;
    }

    public void setPayer(PaymentPayer payer) {
        this.payer = payer;
    }

    public PaymentPayee getPayee() {
        return payee;
    }

    public void setPayee(PaymentPayee payee) {
        this.payee = payee;
    }

    public PaymentAmount getAmount() {
        return amount;
    }

    public void setAmount(PaymentAmount amount) {
        this.amount = amount;
    }

    public PaymentAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(PaymentAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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
