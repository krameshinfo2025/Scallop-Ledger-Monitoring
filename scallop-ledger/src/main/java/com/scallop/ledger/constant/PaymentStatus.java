/**
 ***********************************************************************************
 PaymentStatus.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.constant;

/**
 * Payments are never removed; DELETE moves them to {@link #CANCELLED}, which
 * clients cannot set directly.
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REVERSED,
    CANCELLED
}
