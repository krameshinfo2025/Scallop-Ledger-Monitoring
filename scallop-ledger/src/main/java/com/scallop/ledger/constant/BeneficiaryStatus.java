/**
 ***********************************************************************************
 BeneficiaryStatus.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.constant;

/** Beneficiaries are never removed; DELETE moves them to {@link #DELETED}. */
public enum BeneficiaryStatus {
    ACTIVE,
    DELETED
}
