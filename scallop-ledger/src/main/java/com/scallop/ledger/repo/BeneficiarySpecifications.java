/**
 ***********************************************************************************
 BeneficiarySpecifications.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.scallop.ledger.constant.BeneficiaryStatus;
import com.scallop.ledger.domain.Beneficiary;
import com.scallop.ledger.domain.BeneficiaryAccountHolder;
import com.scallop.ledger.domain.BeneficiaryAddress;
import com.scallop.ledger.domain.BeneficiaryBank;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiarySearchRequest;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/**
 * Turns a {@link BeneficiarySearchRequest} into a JPA Criteria query. Blank
 * filters are ignored; the rest are ANDed together.
 */
public final class BeneficiarySpecifications {

	private static final char LIKE_ESCAPE = '\\';

	private BeneficiarySpecifications() {
	}

	public static Specification<Beneficiary> matching(BeneficiarySearchRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			Join<Beneficiary, BeneficiaryBank> bank = root.join("bank", JoinType.INNER);
			Join<Beneficiary, BeneficiaryAccountHolder> holder = root.join("accountHolder", JoinType.INNER);

			// Soft-deleted rows stay hidden unless the caller asks for them by status.
			if (filter.status() != null) {
				predicates.add(cb.equal(root.get("status"), filter.status()));
			} else {
				predicates.add(cb.notEqual(root.get("status"), BeneficiaryStatus.DELETED));
			}

			equalIfPresent(cb, predicates, root.get("beneficiaryType"), filter.beneficiaryType());
			equalIfPresent(cb, predicates, root.get("currencyCode"), upper(filter.currencyCode()));
			if (filter.isThirdParty() != null) {
				predicates.add(cb.equal(root.get("thirdParty"), filter.isThirdParty()));
			}
			if (filter.referenceId() != null) {
				predicates.add(cb.equal(root.get("referenceId"), filter.referenceId()));
			}

			// Account holder
			if (hasText(filter.holderName())) {
				String pattern = containsPattern(filter.holderName());
				predicates.add(cb.or(
						cb.like(cb.lower(holder.get("firstName")), pattern, LIKE_ESCAPE),
						cb.like(cb.lower(holder.get("lastName")), pattern, LIKE_ESCAPE),
						cb.like(cb.lower(cb.concat(cb.concat(holder.get("firstName"), " "), holder.get("lastName"))),
								pattern, LIKE_ESCAPE),
						cb.like(cb.lower(holder.get("businessName")), pattern, LIKE_ESCAPE)));
			}
			equalIfPresent(cb, predicates, holder.get("type"), upper(filter.holderType()));
			if (hasText(filter.holderEmail())) {
				predicates.add(cb.equal(cb.lower(holder.get("email")), filter.holderEmail().trim().toLowerCase(Locale.ROOT)));
			}
			equalIfPresent(cb, predicates, holder.get("phone"), filter.holderPhone());
			if (hasText(filter.holderCountryCode())) {
				Join<BeneficiaryAccountHolder, BeneficiaryAddress> holderAddress = holder.join("address", JoinType.LEFT);
				predicates.add(cb.equal(holderAddress.get("countryCode"), upper(filter.holderCountryCode())));
			}

			// Bank identifiers
			equalIfPresent(cb, predicates, bank.get("accountNumber"), filter.accountNumber());
			equalIfPresent(cb, predicates, bank.get("iban"), upper(filter.iban()));
			equalIfPresent(cb, predicates, bank.get("bicSwift"), upper(filter.bicSwift()));
			if (hasText(filter.bankName())) {
				predicates.add(cb.like(cb.lower(bank.get("name")), containsPattern(filter.bankName()), LIKE_ESCAPE));
			}
			if (hasText(filter.bankCountryCode())) {
				Join<BeneficiaryBank, BeneficiaryAddress> bankAddress = bank.join("address", JoinType.LEFT);
				predicates.add(cb.equal(bankAddress.get("countryCode"), upper(filter.bankCountryCode())));
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
