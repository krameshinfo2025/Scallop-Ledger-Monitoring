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
 * Which side of the ledger a line falls on, and which side an account
 * increases on. One enum for both, because they are the same two values and
 * comparing them is how a balance gets its sign.
 */
public enum Side {
    DEBIT,
    CREDIT;

    public Side opposite() {
        return this == DEBIT ? CREDIT : DEBIT;
    }
}
