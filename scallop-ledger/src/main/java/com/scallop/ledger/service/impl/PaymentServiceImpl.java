/**
 ***********************************************************************************
 PaymentServiceImpl.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.Beneficiary;
import com.scallop.ledger.domain.FxQuote;
import com.scallop.ledger.domain.Payment;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.BeneficiaryRepository;
import com.scallop.ledger.repo.FxQuoteRepository;
import com.scallop.ledger.repo.LedgerAssetRepository;
import com.scallop.ledger.repo.PaymentRepository;
import com.scallop.ledger.repo.PaymentSpecifications;
import com.scallop.ledger.request.PaymentRequestData.Payee;
import com.scallop.ledger.request.PaymentRequestData.PaymentRequest;
import com.scallop.ledger.request.PaymentRequestData.PaymentResponse;
import com.scallop.ledger.request.PaymentRequestData.PaymentSearchRequest;
import com.scallop.ledger.request.RequestData.Quote;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.PaymentService;
import com.scallop.ledger.service.helper.PaymentMapper;

/**
 * PaymentServiceImpl is a class act as business logic implementation for
 * Payment Operations. Payments are records only: nothing here posts journals
 * or moves wallet balances. Every change is audited in the same transaction.
 *
 */
@Service(value = "paymentService")
public class PaymentServiceImpl implements PaymentService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String ENTITY_TYPE = "PAYMENT";

	/** The Place Holder for paymentRepository of type PaymentRepository */
	private final PaymentRepository paymentRepository;

	/** The Place Holder for fxQuoteRepository of type FxQuoteRepository */
	private final FxQuoteRepository fxQuoteRepository;

	/** The Place Holder for beneficiaryRepository of type BeneficiaryRepository */
	private final BeneficiaryRepository beneficiaryRepository;

	/** The Place Holder for ledgerAssetRepository of type LedgerAssetRepository */
	private final LedgerAssetRepository ledgerAssetRepository;

	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/**
	 * @param paymentRepository
	 * @param fxQuoteRepository
	 * @param beneficiaryRepository
	 * @param ledgerAssetRepository
	 * @param auditService
	 */
	public PaymentServiceImpl(PaymentRepository paymentRepository, FxQuoteRepository fxQuoteRepository,
			BeneficiaryRepository beneficiaryRepository, LedgerAssetRepository ledgerAssetRepository,
			AuditService auditService) {
		super();
		this.paymentRepository = paymentRepository;
		this.fxQuoteRepository = fxQuoteRepository;
		this.beneficiaryRepository = beneficiaryRepository;
		this.ledgerAssetRepository = ledgerAssetRepository;
		this.auditService = auditService;
	}

	@Override
	@Transactional
	public PaymentResponse createPayment(PaymentRequest request, String actor) {

		validate(request);
		String transactionId = trimToNull(request.paymentTransaction().transactionId());
		if (transactionId != null && paymentRepository.existsByTransactionId(transactionId)) {
			throw duplicateTransaction(transactionId);
		}
		FxQuote fxQuote = resolveQuote(request, null);

		Payment payment = new Payment(actor);
		PaymentMapper.apply(request, fxQuote, payment);
		payment = paymentRepository.save(payment);

		auditService.recordIndependently(actor, "PAYMENT_CREATED", ENTITY_TYPE, payment.getId(),
				"type=" + payment.getTransactionType() + ", " + payment.getPayer().getSourceCurrency() + "->"
						+ payment.getPayee().getDestinationCurrency() + ", total=" + payment.getAmount().getTotalAmount());

		return PaymentMapper.toResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getPayment(UUID paymentId, String actor) {

		return PaymentMapper.toResponse(load(paymentId));
	}

	@Override
	@Transactional
	public PaymentResponse updatePayment(UUID paymentId, PaymentRequest request, String actor) {

		Payment payment = loadChangeable(paymentId);

		validate(request);
		String transactionId = trimToNull(request.paymentTransaction().transactionId());
		if (transactionId != null && paymentRepository.existsByTransactionIdAndIdNot(transactionId, paymentId)) {
			throw duplicateTransaction(transactionId);
		}
		FxQuote fxQuote = resolveQuote(request, payment.getFxQuote());

		PaymentMapper.apply(request, fxQuote, payment);
		payment.setUpdatedBy(actor);
		// Flush now so a version clash or constraint failure surfaces here, not after the audit row.
		paymentRepository.saveAndFlush(payment);

		auditService.recordIndependently(actor, "PAYMENT_UPDATED", ENTITY_TYPE, paymentId, "status=" + payment.getStatus());

		return PaymentMapper.toResponse(payment);
	}

	@Override
	@Transactional
	public void deletePayment(UUID paymentId, String actor) {

		Payment payment = loadChangeable(paymentId);
		payment.markCancelled(actor);
		paymentRepository.saveAndFlush(payment);

		auditService.recordIndependently(actor, "PAYMENT_CANCELLED", ENTITY_TYPE, paymentId, null);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<PaymentResponse> searchPayments(Pageable pageable, PaymentSearchRequest filter, String actor) {

		Page<Payment> dataList = paymentRepository.findAll(PaymentSpecifications.matching(filter), pageable);

		List<PaymentResponse> content = dataList.getContent().stream()
				.map(PaymentMapper::toResponse)
				.toList();

		return new PagedResponse<PaymentResponse>(content, pageable.getPageNumber() + 1, content.size(),
				dataList.getTotalElements(), dataList.getTotalPages(), dataList.hasNext());
	}

	private Payment load(UUID paymentId) {
		return paymentRepository.findWithDetailsById(paymentId)
				.orElseThrow(() -> LedgerException.notFound("Payment " + paymentId));
	}

	/** Settled, locked and cancelled payments are final. */
	private Payment loadChangeable(UUID paymentId) {
		Payment payment = load(paymentId);
		if (payment.isFrozen()) {
			String reason = payment.isCancelled() ? "cancelled" : payment.isSettled() ? "settled" : "locked";
			throw LedgerException.conflict("PAYMENT_FINAL",
					"Payment " + paymentId + " is " + reason + " and can no longer be changed.");
		}
		return payment;
	}

	/**
	 * Finds the stored quote the request names. Required when the currencies
	 * differ; it must convert payer currency into payee currency and, unless it
	 * is the quote the payment already had, must not have expired.
	 */
	private FxQuote resolveQuote(PaymentRequest request, FxQuote current) {

		String sourceCurrency = request.payer().sourceCurrency();
		String destinationCurrency = request.payee().destinationCurrency();
		Quote quote = request.quote();

		if (quote == null) {
			if (!sourceCurrency.equals(destinationCurrency)) {
				throw LedgerException.badRequest("QUOTE_REQUIRED",
						"A quote is required to pay " + sourceCurrency + " into " + destinationCurrency + ".");
			}
			return null;
		}
		if (isBlank(quote.quoteId()) || isBlank(quote.entityId())) {
			throw LedgerException.badRequest("QUOTE_REQUIRED", "quote needs a quoteId and an entityId.");
		}

		FxQuote fxQuote = fxQuoteRepository
				.findFirstByQuoteIdAndEntityIdOrderByObservedAtDesc(quote.quoteId().trim(), quote.entityId().trim())
				.orElseThrow(() -> LedgerException.badRequest("UNKNOWN_QUOTE",
						"Quote " + quote.quoteId() + " for entity " + quote.entityId() + " does not exist."));

		if (!fxQuote.getBaseCurrency().equals(sourceCurrency)
				|| !fxQuote.getTargetCurrency().equals(destinationCurrency)) {
			throw LedgerException.badRequest("QUOTE_CURRENCY_MISMATCH",
					"Quote " + fxQuote.getQuoteId() + " converts " + fxQuote.getBaseCurrency() + " to "
							+ fxQuote.getTargetCurrency() + ", but the payment is " + sourceCurrency + " to "
							+ destinationCurrency + ".");
		}

		// Re-saving a payment with the quote it was priced with is fine after that quote expires.
		boolean unchanged = current != null && current.getId().equals(fxQuote.getId());
		if (!unchanged && !fxQuote.getExpiresAt().isAfter(Instant.now())) {
			throw LedgerException.badRequest("QUOTE_EXPIRED",
					"Quote " + fxQuote.getQuoteId() + " expired at " + fxQuote.getExpiresAt() + ".");
		}
		return fxQuote;
	}

	/** Rules that span fields, or need the database, and so cannot be bean-validation annotations. */
	private void validate(PaymentRequest request) {

		requireActiveCurrency(request.payer().sourceCurrency());
		requireActiveCurrency(request.payee().destinationCurrency());

		Payee payee = request.payee();
		if (Boolean.TRUE.equals(payee.isBeneficiaryInSytem())) {
			requireActiveBeneficiary(payee);
		} else {
			if (isBlank(payee.destinationWalletId()) && isBlank(payee.accountNumberOrIBAN())) {
				throw LedgerException.badRequest("PAYEE_DESTINATION_REQUIRED",
						"payee needs a destinationWalletId, an accountNumberOrIBAN, or an in-system beneficiary.");
			}
			if (!isBlank(payee.accountNumberOrIBAN()) && isBlank(payee.beneficiaryName())) {
				throw LedgerException.badRequest("BENEFICIARY_NAME_REQUIRED",
						"A payout to accountNumberOrIBAN needs a beneficiaryName.");
			}
		}
	}

	private void requireActiveCurrency(String currency) {
		boolean currencyActive = ledgerAssetRepository.findById(currency)
				.map(asset -> Boolean.TRUE.equals(asset.getActive()))
				.orElse(false);
		if (!currencyActive) {
			throw LedgerException.badRequest("UNKNOWN_CURRENCY", "Currency " + currency + " is not an active ledger asset.");
		}
	}

	private void requireActiveBeneficiary(Payee payee) {
		if (isBlank(payee.beneficiaryId())) {
			throw LedgerException.badRequest("BENEFICIARY_REQUIRED",
					"isBeneficiaryInSytem is true, so payee needs a beneficiaryId.");
		}
		UUID beneficiaryId;
		try {
			beneficiaryId = UUID.fromString(payee.beneficiaryId().trim());
		} catch (IllegalArgumentException ex) {
			throw LedgerException.badRequest("UNKNOWN_BENEFICIARY", "beneficiaryId is not a beneficiary id.");
		}
		Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
				.filter(found -> !found.isDeleted())
				.orElseThrow(() -> LedgerException.badRequest("UNKNOWN_BENEFICIARY",
						"Beneficiary " + beneficiaryId + " does not exist or was deleted."));
		if (!beneficiary.getCurrencyCode().equals(payee.destinationCurrency())) {
			throw LedgerException.badRequest("BENEFICIARY_CURRENCY_MISMATCH", "Beneficiary " + beneficiaryId
					+ " is paid in " + beneficiary.getCurrencyCode() + ", not " + payee.destinationCurrency() + ".");
		}
	}

	private static LedgerException duplicateTransaction(String transactionId) {
		return LedgerException.conflict("DUPLICATE_TRANSACTION",
				"A payment with transactionId " + transactionId + " already exists.");
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private static String trimToNull(String value) {
		return isBlank(value) ? null : value.trim();
	}
}
