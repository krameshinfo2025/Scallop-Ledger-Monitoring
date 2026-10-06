/**
 ***********************************************************************************
 AppUserRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scallop.ledger.domain.JournalControl;

public interface JournalControlRepository extends JpaRepository<JournalControl, String> {

	Optional<JournalControl> findByEntityIdAndRequestKey(String entityId, String requestKey);

	boolean existsByEntityIdAndRequestKey(String entityId, String requestKey);

	long countByOriginalJournalIdAndEventKindIn(String originalJournalId, Collection<String> eventKinds);

	long countByEntityId(String entityId);
}