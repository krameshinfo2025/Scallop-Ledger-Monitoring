package com.scallop.ledger.constant;

/**
 * Platform-wide role, distinct from {@link MembershipRole}. This says what a
 * login is on the platform; MembershipRole says what it may do to a particular
 * holder's money.
 */
public enum PlatformRole {
    CUSTOMER,
    ADMIN
}
