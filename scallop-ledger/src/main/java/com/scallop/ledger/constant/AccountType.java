/**
 ***********************************************************************************
 AppUser.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.constant;

/**
 * The five classical account types. Which side an account increases on follows
 * from its type, and the ledger_account_side_matches_type check constraint
 * enforces the pairing in the database.
 */
public enum AccountType {

    ASSET(Side.DEBIT),
    EXPENSE(Side.DEBIT),
    LIABILITY(Side.CREDIT),
    EQUITY(Side.CREDIT),
    INCOME(Side.CREDIT);

    private final Side normalSide;

    AccountType(Side normalSide) {
        this.normalSide = normalSide;
    }

    /** The side on which this type of account increases. */
    public Side normalSide() {
        return normalSide;
    }
}
