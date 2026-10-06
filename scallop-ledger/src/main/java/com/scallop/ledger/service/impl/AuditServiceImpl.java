/**
 ***********************************************************************************
 AuditService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.AuditLog;
import com.scallop.ledger.repo.AuditLogRepository;
import com.scallop.ledger.service.AuditService;

/**
 * 
 */
@Service(value="auditService")
public class AuditServiceImpl implements AuditService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for auditLogRepository of type  AuditLogRepository */
	 private final AuditLogRepository auditLogRepository;

	/**
	 * @param auditLogRepository
	 */
	public AuditServiceImpl(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	
    /** Joins the caller's transaction, so the audit entry commits or rolls back with the change. */
    @Transactional
    public void record(String actor, String action, String entityType, Object entityId, String detail) {
    	
    	AuditLog auditLog = new AuditLog(actor, action, entityType,
                entityId == null ? null : entityId.toString(), detail, Instant.now());
    	
    	auditLogRepository.save(auditLog);    	
    
    }

    /**
     * For events that must be kept even when the surrounding work fails -
     * a failed login is the obvious one.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordIndependently(String actor, String action, String entityType, Object entityId,
            String detail) {
        record(actor, action, entityType, entityId, detail);
    }



	 
	 

}
