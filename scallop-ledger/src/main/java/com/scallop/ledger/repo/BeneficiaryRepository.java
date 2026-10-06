/**
 ***********************************************************************************
 BeneficiaryRepository.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.scallop.ledger.domain.Beneficiary;

/**
 * Every read fetches the whole beneficiary graph (bank, holder and both
 * addresses) in one query, so mapping a page to responses causes no N+1 loads.
 */
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID>, JpaSpecificationExecutor<Beneficiary> {

	@EntityGraph(attributePaths = { "bank", "bank.address", "accountHolder", "accountHolder.address" })
	Optional<Beneficiary> findWithDetailsById(UUID id);

	@Override
	@EntityGraph(attributePaths = { "bank", "bank.address", "accountHolder", "accountHolder.address" })
	Page<Beneficiary> findAll(Specification<Beneficiary> spec, Pageable pageable);

}
