/**
 ***********************************************************************************
 AuditService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

/**
 * 
 */
public interface AuditService {

	void recordIndependently(String actor, String action, String entityType, Object entityId, String detail);

}
