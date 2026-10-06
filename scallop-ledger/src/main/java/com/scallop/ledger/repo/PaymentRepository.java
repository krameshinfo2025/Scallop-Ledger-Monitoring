/**
 ***********************************************************************************
 PaymentRepository.Java
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

import com.scallop.ledger.domain.Payment;

/**
 * Every read fetches the whole payment graph (payer, payee, amount, additional
 * info and quote) in one query. All of them are to-one, so paging still happens
 * in the database and mapping a page to responses causes no N+1 loads.
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

	@EntityGraph(attributePaths = { "payer", "payee", "amount", "additionalInfo", "fxQuote" })
	Optional<Payment> findWithDetailsById(UUID id);

	@Override
	@EntityGraph(attributePaths = { "payer", "payee", "amount", "additionalInfo", "fxQuote" })
	Page<Payment> findAll(Specification<Payment> spec, Pageable pageable);

	boolean existsByTransactionId(String transactionId);

	boolean existsByTransactionIdAndIdNot(String transactionId, UUID id);

}
