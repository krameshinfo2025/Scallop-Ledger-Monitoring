/**
 ***********************************************************************************
 BeneficiaryServiceImpl.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.Beneficiary;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.BeneficiaryRepository;
import com.scallop.ledger.repo.BeneficiarySpecifications;
import com.scallop.ledger.repo.LedgerAssetRepository;
import com.scallop.ledger.request.BeneficiaryRequestData.AccountHolderDetails;
import com.scallop.ledger.request.BeneficiaryRequestData.BankDetails;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryRequest;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryResponse;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiarySearchRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.BeneficiaryService;
import com.scallop.ledger.service.helper.BeneficiaryMapper;

/**
 * BeneficiaryServiceImpl is a class act as business logic implementation for
 * Beneficiary Operations. Every change is audited in the same transaction.
 *
 */
@Service(value = "beneficiaryService")
public class BeneficiaryServiceImpl implements BeneficiaryService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String ENTITY_TYPE = "BENEFICIARY";

	/** The Place Holder for beneficiaryRepository of type BeneficiaryRepository */
	private final BeneficiaryRepository beneficiaryRepository;

	/** The Place Holder for ledgerAssetRepository of type LedgerAssetRepository */
	private final LedgerAssetRepository ledgerAssetRepository;

	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/**
	 * @param beneficiaryRepository
	 * @param ledgerAssetRepository
	 * @param auditService
	 */
	public BeneficiaryServiceImpl(BeneficiaryRepository beneficiaryRepository,
			LedgerAssetRepository ledgerAssetRepository, AuditService auditService) {
		super();
		this.beneficiaryRepository = beneficiaryRepository;
		this.ledgerAssetRepository = ledgerAssetRepository;
		this.auditService = auditService;
	}

	@Override
	@Transactional
	public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request, String actor) {

		validate(request);

		Beneficiary beneficiary = new Beneficiary(actor);
		BeneficiaryMapper.apply(request, beneficiary);
		beneficiary = beneficiaryRepository.save(beneficiary);

		auditService.recordIndependently(actor, "BENEFICIARY_CREATED", ENTITY_TYPE, beneficiary.getId(),
				"currency=" + beneficiary.getCurrencyCode() + ", type=" + beneficiary.getBeneficiaryType());

		return BeneficiaryMapper.toResponse(beneficiary);
	}

	@Override
	@Transactional(readOnly = true)
	public BeneficiaryResponse getBeneficiary(UUID beneficiaryId, String actor) {

		return BeneficiaryMapper.toResponse(loadActive(beneficiaryId));
	}

	@Override
	@Transactional
	public BeneficiaryResponse updateBeneficiary(UUID beneficiaryId, BeneficiaryRequest request, String actor) {

		validate(request);

		Beneficiary beneficiary = loadActive(beneficiaryId);
		BeneficiaryMapper.apply(request, beneficiary);
		beneficiary.setUpdatedBy(actor);
		// Flush now so a version clash or constraint failure surfaces here, not after the audit row.
		beneficiaryRepository.saveAndFlush(beneficiary);

		auditService.recordIndependently(actor, "BENEFICIARY_UPDATED", ENTITY_TYPE, beneficiaryId, null);

		return BeneficiaryMapper.toResponse(beneficiary);
	}

	@Override
	@Transactional
	public void deleteBeneficiary(UUID beneficiaryId, String actor) {

		Beneficiary beneficiary = loadActive(beneficiaryId);
		beneficiary.markDeleted(actor);
		beneficiaryRepository.saveAndFlush(beneficiary);

		auditService.recordIndependently(actor, "BENEFICIARY_DELETED", ENTITY_TYPE, beneficiaryId, null);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<BeneficiaryResponse> searchBeneficiaries(Pageable pageable, BeneficiarySearchRequest filter,
			String actor) {

		Page<Beneficiary> dataList = beneficiaryRepository.findAll(BeneficiarySpecifications.matching(filter), pageable);

		List<BeneficiaryResponse> content = dataList.getContent().stream()
				.map(BeneficiaryMapper::toResponse)
				.toList();

		return new PagedResponse<BeneficiaryResponse>(content, pageable.getPageNumber() + 1, content.size(),
				dataList.getTotalElements(), dataList.getTotalPages(), dataList.hasNext());
	}

	private Beneficiary loadActive(UUID beneficiaryId) {
		return beneficiaryRepository.findWithDetailsById(beneficiaryId)
				.filter(beneficiary -> !beneficiary.isDeleted())
				.orElseThrow(() -> LedgerException.notFound("Beneficiary " + beneficiaryId));
	}

	/** Rules that span fields, or need the database, and so cannot be bean-validation annotations. */
	private void validate(BeneficiaryRequest request) {

		boolean currencyActive = ledgerAssetRepository.findById(request.currencyCode())
				.map(asset -> Boolean.TRUE.equals(asset.getActive()))
				.orElse(false);
		if (!currencyActive) {
			throw LedgerException.badRequest("UNKNOWN_CURRENCY",
					"Currency " + request.currencyCode() + " is not an active ledger asset.");
		}

		BankDetails bank = request.bank();
		if (isBlank(bank.accountNumber()) && isBlank(bank.iban())) {
			throw LedgerException.badRequest("BANK_ACCOUNT_REQUIRED",
					"Bank details need an accountNumber or an iban.");
		}

		AccountHolderDetails holder = request.accountHolder();
		if ("BUSINESS".equals(holder.type()) && isBlank(holder.businessName())) {
			throw LedgerException.badRequest("BUSINESS_NAME_REQUIRED",
					"A BUSINESS account holder needs a businessName.");
		}
		if ("INDIVIDUAL".equals(holder.type()) && (isBlank(holder.firstName()) || isBlank(holder.lastName()))) {
			throw LedgerException.badRequest("HOLDER_NAME_REQUIRED",
					"An INDIVIDUAL account holder needs a firstName and lastName.");
		}

		if (holder.dateOfBirth() != null) {
			LocalDate dateOfBirth;
			try {
				dateOfBirth = LocalDate.parse(holder.dateOfBirth());
			} catch (DateTimeException ex) {
				throw LedgerException.badRequest("INVALID_DATE_OF_BIRTH", "dateOfBirth is not a real date.");
			}
			if (dateOfBirth.isAfter(LocalDate.now())) {
				throw LedgerException.badRequest("INVALID_DATE_OF_BIRTH", "dateOfBirth cannot be in the future.");
			}
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
