/**
 ***********************************************************************************
 AppUserRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.math.BigDecimal;

/** Per-currency platform figures. */
public interface CurrencyStatsRow {

    String getCurrencyCode();

    long getWalletCount();

    /** Total held on behalf of customers - the bank's liability in this currency. */
    BigDecimal getCustomerBalance();

    BigDecimal getFeeIncome();

    /** The bank's own funds in this currency. */
    BigDecimal getSettlementBalance();

    long getEntryCount();

    /** Gross value of everything ever posted in this currency, fees included. */
    BigDecimal getPostedVolume();
}
