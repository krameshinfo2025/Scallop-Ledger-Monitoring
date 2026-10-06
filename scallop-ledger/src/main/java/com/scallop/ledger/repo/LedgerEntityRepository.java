/**
 ***********************************************************************************
 LedgerEntitiyRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scallop.ledger.domain.LedgerEntity;

/**
 * 
 */
public interface LedgerEntityRepository extends JpaRepository<LedgerEntity, String> {

}
