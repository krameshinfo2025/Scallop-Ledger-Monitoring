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
 * A user's authority over one holder's money.
 *
 * <p>OWNER and ACCOUNTANT may move money; VIEWER may only read. An individual
 * holder always has exactly one OWNER and nothing else. A business may have
 * several members, but still only one OWNER - the database enforces that with
 * a partial unique index.
 */
public enum MembershipRole {
    OWNER,
    ACCOUNTANT,
    VIEWER;

    /** Whether this role may initiate a debit against the holder's wallets. */
    public boolean canMoveMoney() {
        return this == OWNER || this == ACCOUNTANT;
    }
}
