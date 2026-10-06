/**
 ***********************************************************************************
 ReconciliationServiceImpl.Java
 version 1.0
 24,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.ReserveObservation;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.LedgerRepository;
import com.scallop.ledger.repo.ReserveObservationRepository;
import com.scallop.ledger.request.RequestData.Observation;
import com.scallop.ledger.response.ResponseData.ReconciliationResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.ReconciliationService;
import com.scallop.ledger.service.helper.JournalFactory;

/**
 * ReconciliationServiceImpl is a Class for fetch Reconciliations of all the
 * users.
 *
 */
@Service(value = "reconciliationService")
public class ReconciliationServiceImpl implements ReconciliationService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/** The Place Holder for ledgerRepository of type LedgerRepository */
	private final LedgerRepository ledgerRepository;

	/** The Place Holder for reserveObservationRepository of type ReserveObservationRepository */
	private final ReserveObservationRepository reserveObservationRepository;

	/**
	 * @param auditService
	 * @param ledgerRepository
	 * @param reserveObservationRepository
	 */
	public ReconciliationServiceImpl(final AuditService auditService,
			                         final LedgerRepository ledgerRepository,
			                         final ReserveObservationRepository reserveObservationRepository) {
		this.auditService = auditService;
		this.ledgerRepository = ledgerRepository;
		this.reserveObservationRepository = reserveObservationRepository;
	}

	/**
	 * Method for fetch all Reconciliations.
	 *
	 * @return data containing the Reconciliations from the service layer.
	 * @throws LedgerException if any
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReserveObservation> getReconciliations() {

		// fetch the 100 most recent Reconciliations
		return reserveObservationRepository.findTop100ByOrderByObservedAtDesc();

	}

	/**
	 * Method for save Reconciliation details in the System.
	 *
	 * @param quote          - user input for Reconciliations of type
	 *                       RequestData.Observation.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 *
	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any
	 */
	@Override
	@Transactional
	public ReconciliationResponse saveReconcile(final Observation reconcillation, final String actor)
			throws LedgerException {

		if (reconcillation.account() == null || !reconcillation.account().startsWith("CLEARING:")
				|| reconcillation.statementReference() == null || reconcillation.statementReference().isBlank()
				|| reconcillation.statementReference().length() > 200 || reconcillation.observedAt() == null
				|| reconcillation.observedAt().isAfter(Instant.now())) {

			throw LedgerException.invalid("Historical clearing account and statement reference required");
		}

		if (reconcillation.externalBalance() == null) {
			throw LedgerException.invalid("External balance required");
		}

		BigDecimal external = JournalFactory.money(reconcillation.externalBalance().abs(), false)
				.multiply(BigDecimal.valueOf(reconcillation.externalBalance().signum()));

		// Statements use debit-positive clearing convention; customers use
		// credit-positive convention.
		BigDecimal ledger = ledgerRepository
				.balance(reconcillation.entityId(),
						reconcillation.account(), reconcillation.currency(), 
						reconcillation.observedAt()).negate(),
				difference = external.subtract(ledger);
		
		ReserveObservation reserveObservation = new ReserveObservation(UUID.randomUUID(), reconcillation.entityId(),
				reconcillation.account(), reconcillation.currency(), reconcillation.statementReference(),
				reconcillation.observedAt(), external, ledger, difference, actor);

		reserveObservation = reserveObservationRepository.saveAndFlush(reserveObservation);
		
		auditService.recordIndependently(actor, "RECONCILE", reserveObservation.getEntityId(), reserveObservation.getObservationId(),
				reserveObservation.getAccount() + " as " + reserveObservation.getObservationId());
		
		ReconciliationResponse response = new ReconciliationResponse(reserveObservation.getObservationId(),
				ledger.toPlainString(),
				difference.toPlainString(),
				difference.signum() == 0);

		return response;
	}

}
