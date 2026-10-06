/**
 ***********************************************************************************
 PaymentAmount.Java
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

/** The principal, fee and total breakdown of one {@link Payment}. */
@Entity
@Table(name = "payment_amount")
public class PaymentAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "principle_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal principleAmount;

    @Column(name = "fee_amount", precision = 38, scale = 18)
    private BigDecimal feeAmount;

    @Column(name = "network_fee", precision = 38, scale = 18)
    private BigDecimal networkFee;

    @Column(name = "other_fee", precision = 38, scale = 18)
    private BigDecimal otherFee;

    @Column(name = "total_fee", precision = 38, scale = 18)
    private BigDecimal totalFee;

    @Column(name = "fee_in_usd", precision = 38, scale = 18)
    private BigDecimal feeInUsd;

    @Column(name = "discount", precision = 38, scale = 18)
    private BigDecimal discount;

    @Column(name = "net_gross_amount", precision = 38, scale = 18)
    private BigDecimal netGrossAmount;

    @Column(name = "total_amount", nullable = false, precision = 38, scale = 18)
    private BigDecimal totalAmount;

    public PaymentAmount() {
        // for JPA and the mapper
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getPrincipleAmount() {
        return principleAmount;
    }

    public void setPrincipleAmount(BigDecimal principleAmount) {
        this.principleAmount = principleAmount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getFeeInUsd() {
        return feeInUsd;
    }

    public void setFeeInUsd(BigDecimal feeInUsd) {
        this.feeInUsd = feeInUsd;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getNetGrossAmount() {
        return netGrossAmount;
    }

    public void setNetGrossAmount(BigDecimal netGrossAmount) {
        this.netGrossAmount = netGrossAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
