/**
 ***********************************************************************************
 AuditLogRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scallop.ledger.domain.AuditLog;

/**
 * 
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(String entityType, String entityId);

    Page<AuditLog> findAllByOrderByOccurredAtDesc(Pageable pageable);

}
