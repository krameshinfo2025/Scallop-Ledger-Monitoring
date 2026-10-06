/**
 ***********************************************************************************
 PaymentSpecifications.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.scallop.ledger.constant.PaymentStatus;
import com.scallop.ledger.domain.FxQuote;
import com.scallop.ledger.domain.Payment;
import com.scallop.ledger.domain.PaymentAmount;
import com.scallop.ledger.domain.PaymentPayee;
import com.scallop.ledger.domain.PaymentPayer;
import com.scallop.ledger.request.PaymentRequestData.PaymentSearchRequest;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/**
 * Turns a {@link PaymentSearchRequest} into a JPA Criteria query. Blank
 * filters are ignored; the rest are ANDed together.
 */
public final class PaymentSpecifications {

	private static final char LIKE_ESCAPE = '\\';

	private PaymentSpecifications() {
	}

	public static Specification<Payment> matching(PaymentSearchRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			Join<Payment, PaymentPayer> payer = root.join("payer", JoinType.INNER);
			Join<Payment, PaymentPayee> payee = root.join("payee", JoinType.INNER);
			Join<Payment, PaymentAmount> amount = root.join("amount", JoinType.INNER);

			// Cancelled rows stay hidden unless the caller asks for them by status.
			if (filter.status() != null) {
				predicates.add(cb.equal(root.get("status"), filter.status()));
			} else {
				predicates.add(cb.notEqual(root.get("status"), PaymentStatus.CANCELLED));
			}

			// Transaction
			equalIfPresent(cb, predicates, root.get("transactionId"), filter.transactionId());
			equalIfPresent(cb, predicates, root.get("originalTransactionId"), filter.originalTransactionId());
			equalIfPresent(cb, predicates, root.get("transactionHash"), filter.transactionHash());
			equalIfPresent(cb, predicates, root.get("transactionType"), filter.transactionType());
			equalIfPresent(cb, predicates, root.get("transferType"), filter.transferType());
			if (filter.isSettled() != null) {
				predicates.add(cb.equal(root.get("settled"), filter.isSettled()));
			}
			if (filter.isLocked() != null) {
				predicates.add(cb.equal(root.get("locked"), filter.isLocked()));
			}
			if (filter.createdFrom() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.createdFrom()));
			}
			if (filter.createdTo() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.createdTo()));
			}

			// Payer
			equalIfPresent(cb, predicates, payer.get("userId"), filter.userId());
			equalIfPresent(cb, predicates, payer.get("sourceWalletId"), filter.sourceWalletId());
			equalIfPresent(cb, predicates, payer.get("sourceCurrency"), upper(filter.sourceCurrency()));

			// Payee
			equalIfPresent(cb, predicates, payee.get("destinationWalletId"), filter.destinationWalletId());
			equalIfPresent(cb, predicates, payee.get("destinationCurrency"), upper(filter.destinationCurrency()));
			equalIfPresent(cb, predicates, payee.get("beneficiaryId"), filter.beneficiaryId());
			if (hasText(filter.beneficiaryName())) {
				predicates.add(cb.like(cb.lower(payee.get("beneficiaryName")), containsPattern(filter.beneficiaryName()),
						LIKE_ESCAPE));
			}

			// Amount
			Expression<BigDecimal> totalAmount = amount.get("totalAmount");
			if (filter.minTotalAmount() != null) {
				predicates.add(cb.greaterThanOrEqualTo(totalAmount, filter.minTotalAmount()));
			}
			if (filter.maxTotalAmount() != null) {
				predicates.add(cb.lessThanOrEqualTo(totalAmount, filter.maxTotalAmount()));
			}

			// Quote
			if (hasText(filter.quoteId())) {
				Join<Payment, FxQuote> quote = root.join("fxQuote", JoinType.INNER);
				predicates.add(cb.equal(quote.get("quoteId"), filter.quoteId().trim()));
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}

	private static void equalIfPresent(CriteriaBuilder cb, List<Predicate> predicates, Expression<?> path, String value) {
		if (hasText(value)) {
			predicates.add(cb.equal(path, value.trim()));
		}
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private static String upper(String value) {
		return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
	}

	/** Case-insensitive "contains", with the user's own % and _ treated literally. */
	private static String containsPattern(String value) {
		String escaped = value.trim().toLowerCase(Locale.ROOT)
				.replace("\\", "\\\\")
				.replace("%", "\\%")
				.replace("_", "\\_");
		return "%" + escaped + "%";
	}
}
