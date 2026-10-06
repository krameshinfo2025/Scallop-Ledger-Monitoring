/**
 ***********************************************************************************
 PaymentAdditionalInfo.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Optional memo, rail and fee-rule details of one {@link Payment}. */
@Entity
@Table(name = "payment_additional_info")
public class PaymentAdditionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "network", length = 32)
    private String network;

    @Column(name = "provider", length = 64)
    private String provider;

    @Column(name = "include_network_fee")
    private Boolean includeNetworkFee;

    @Column(name = "fee_collected_from", length = 32)
    private String feeCollectedFrom;

    @Column(name = "fee_rule_type", length = 32)
    private String feeRuleType;

    @Column(name = "fee_rule_percent", precision = 9, scale = 6)
    private BigDecimal feeRulePercent;

    @Column(name = "free_rule_amount", precision = 38, scale = 18)
    private BigDecimal freeRuleAmount;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "reference", length = 140)
    private String reference;

    @Column(name = "funding_instructions", length = 2000)
    private String fundingInstructions;

    public PaymentAdditionalInfo() {
        // for JPA and the mapper
    }

    public UUID getId() {
        return id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getIncludeNetworkFee() {
        return includeNetworkFee;
    }

    public void setIncludeNetworkFee(Boolean includeNetworkFee) {
        this.includeNetworkFee = includeNetworkFee;
    }

    public String getFeeCollectedFrom() {
        return feeCollectedFrom;
    }

    public void setFeeCollectedFrom(String feeCollectedFrom) {
        this.feeCollectedFrom = feeCollectedFrom;
    }

    public String getFeeRuleType() {
        return feeRuleType;
    }

    public void setFeeRuleType(String feeRuleType) {
        this.feeRuleType = feeRuleType;
    }

    public BigDecimal getFeeRulePercent() {
        return feeRulePercent;
    }

    public void setFeeRulePercent(BigDecimal feeRulePercent) {
        this.feeRulePercent = feeRulePercent;
    }

    public BigDecimal getFreeRuleAmount() {
        return freeRuleAmount;
    }

    public void setFreeRuleAmount(BigDecimal freeRuleAmount) {
        this.freeRuleAmount = freeRuleAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFundingInstructions() {
        return fundingInstructions;
    }

    public void setFundingInstructions(String fundingInstructions) {
        this.fundingInstructions = fundingInstructions;
    }
}
