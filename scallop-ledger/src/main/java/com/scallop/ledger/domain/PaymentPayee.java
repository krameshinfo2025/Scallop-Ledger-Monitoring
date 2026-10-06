/**
 ***********************************************************************************
 PaymentPayee.Java
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

/**
 * Where the money lands: a wallet, a beneficiary already in the system, or a
 * bank account given inline. Owned by exactly one {@link Payment}.
 */
@Entity
@Table(name = "payment_payee")
public class PaymentPayee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "destination_wallet_id", length = 64)
    private String destinationWalletId;

    @Column(name = "destination_currency", nullable = false, length = 10)
    private String destinationCurrency;

    @Column(name = "destination_amount", precision = 38, scale = 18)
    private BigDecimal destinationAmount;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "destination_image", length = 2048)
    private String destinationImage;

    @Column(name = "is_beneficiary_in_system", nullable = false)
    private boolean beneficiaryInSystem;

    @Column(name = "beneficiary_id", length = 64)
    private String beneficiaryId;

    @Column(name = "beneficiary_name", length = 140)
    private String beneficiaryName;

    @Column(name = "account_number_or_iban", length = 40)
    private String accountNumberOrIban;

    @Column(name = "code_swift_or_bic", length = 11)
    private String codeSwiftOrBic;

    @Column(name = "code_routing_or_local", length = 20)
    private String codeRoutingOrLocal;

    @Column(name = "bank_address", length = 500)
    private String bankAddress;

    public PaymentPayee() {
        // for JPA and the mapper
    }

    public UUID getId() {
        return id;
    }

    public String getDestinationWalletId() {
        return destinationWalletId;
    }

    public void setDestinationWalletId(String destinationWalletId) {
        this.destinationWalletId = destinationWalletId;
    }

    public String getDestinationCurrency() {
        return destinationCurrency;
    }

    public void setDestinationCurrency(String destinationCurrency) {
        this.destinationCurrency = destinationCurrency;
    }

    public BigDecimal getDestinationAmount() {
        return destinationAmount;
    }

    public void setDestinationAmount(BigDecimal destinationAmount) {
        this.destinationAmount = destinationAmount;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationImage() {
        return destinationImage;
    }

    public void setDestinationImage(String destinationImage) {
        this.destinationImage = destinationImage;
    }

    public boolean isBeneficiaryInSystem() {
        return beneficiaryInSystem;
    }

    public void setBeneficiaryInSystem(boolean beneficiaryInSystem) {
        this.beneficiaryInSystem = beneficiaryInSystem;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountNumberOrIban() {
        return accountNumberOrIban;
    }

    public void setAccountNumberOrIban(String accountNumberOrIban) {
        this.accountNumberOrIban = accountNumberOrIban;
    }

    public String getCodeSwiftOrBic() {
        return codeSwiftOrBic;
    }

    public void setCodeSwiftOrBic(String codeSwiftOrBic) {
        this.codeSwiftOrBic = codeSwiftOrBic;
    }

    public String getCodeRoutingOrLocal() {
        return codeRoutingOrLocal;
    }

    public void setCodeRoutingOrLocal(String codeRoutingOrLocal) {
        this.codeRoutingOrLocal = codeRoutingOrLocal;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }
}
