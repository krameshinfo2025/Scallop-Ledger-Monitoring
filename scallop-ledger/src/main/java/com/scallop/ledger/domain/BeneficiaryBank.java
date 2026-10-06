/**
 ***********************************************************************************
 BeneficiaryBank.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * The bank account a beneficiary is paid into. Which routing fields are filled
 * depends on the payout rail (IFSC for India, sort code for the UK, CLABE for
 * Mexico and so on); the database only insists on an account number or IBAN.
 */
@Entity
@Table(name = "beneficiary_bank")
public class BeneficiaryBank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private BeneficiaryAddress address;

    @Column(name = "type")
    private String type;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "routing_number")
    private String routingNumber;

    @Column(name = "transit_number")
    private String transitNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "sort_code")
    private String sortCode;

    @Column(name = "bsb_number")
    private String bsbNumber;

    @Column(name = "ncc_number")
    private String nccNumber;

    @Column(name = "clabe_number")
    private String clabeNumber;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "cnaps_code")
    private String cnapsCode;

    @Column(name = "nuban_code")
    private String nubanCode;

    @Column(name = "clearing_code")
    private String clearingCode;

    @Column(name = "pix_code")
    private String pixCode;

    @Column(name = "iban")
    private String iban;

    @Column(name = "bic_swift")
    private String bicSwift;

    public BeneficiaryBank() {
        // for JPA and the mapper
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeneficiaryAddress getAddress() {
        return address;
    }

    public void setAddress(BeneficiaryAddress address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getTransitNumber() {
        return transitNumber;
    }

    public void setTransitNumber(String transitNumber) {
        this.transitNumber = transitNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getBsbNumber() {
        return bsbNumber;
    }

    public void setBsbNumber(String bsbNumber) {
        this.bsbNumber = bsbNumber;
    }

    public String getNccNumber() {
        return nccNumber;
    }

    public void setNccNumber(String nccNumber) {
        this.nccNumber = nccNumber;
    }

    public String getClabeNumber() {
        return clabeNumber;
    }

    public void setClabeNumber(String clabeNumber) {
        this.clabeNumber = clabeNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCnapsCode() {
        return cnapsCode;
    }

    public void setCnapsCode(String cnapsCode) {
        this.cnapsCode = cnapsCode;
    }

    public String getNubanCode() {
        return nubanCode;
    }

    public void setNubanCode(String nubanCode) {
        this.nubanCode = nubanCode;
    }

    public String getClearingCode() {
        return clearingCode;
    }

    public void setClearingCode(String clearingCode) {
        this.clearingCode = clearingCode;
    }

    public String getPixCode() {
        return pixCode;
    }

    public void setPixCode(String pixCode) {
        this.pixCode = pixCode;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBicSwift() {
        return bicSwift;
    }

    public void setBicSwift(String bicSwift) {
        this.bicSwift = bicSwift;
    }
}
