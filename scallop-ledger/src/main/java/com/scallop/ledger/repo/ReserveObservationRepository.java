package com.scallop.ledger.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scallop.ledger.domain.ReserveObservation;

public interface ReserveObservationRepository extends JpaRepository<ReserveObservation, UUID> {

	Optional<ReserveObservation> findFirstByEntityIdAndAccountAndCurrencyOrderByObservedAtDesc(String entityId,
			String account, String currency);

	List<ReserveObservation> findTop100ByOrderByObservedAtDesc();
}