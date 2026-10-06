/**
 ***********************************************************************************
 Role.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.constant;

/**
 * Manages users, rules and models.
 */
public enum Role {
   
	/** Manages users, rules and models. */
    ADMIN,
    /** Works alerts and cases. */
    ANALYST,
    /** A calling system (the ledger) that submits events and asks for decisions. */
    CLIENT_SYSTEM
}
