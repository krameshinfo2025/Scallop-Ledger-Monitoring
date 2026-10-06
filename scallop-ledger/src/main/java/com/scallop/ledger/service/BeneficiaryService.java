/**
 ***********************************************************************************
 BeneficiaryService.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryRequest;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryResponse;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiarySearchRequest;
import com.scallop.ledger.response.PagedResponse;

/**
 * BeneficiaryService is an interface act as business logic implementation
 * for Beneficiary Operations
 *
 */
public interface BeneficiaryService {

	/**
	 * Create a beneficiary with its bank and account holder details.
	 *
	 * @param request - the BeneficiaryRequest passed in the request body.
	 * @param actor   - the authenticated user name.
	 *
	 * @return the created beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException if the request breaks a business rule
	 */
	BeneficiaryResponse createBeneficiary(BeneficiaryRequest request, String actor);

	/**
	 * Fetch one beneficiary that has not been deleted.
	 *
	 * @param beneficiaryId - the beneficiary identifier.
	 * @param actor         - the authenticated user name.
	 *
	 * @return the beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist or was deleted
	 */
	BeneficiaryResponse getBeneficiary(UUID beneficiaryId, String actor);

	/**
	 * Replace every field of a beneficiary (PUT semantics).
	 *
	 * @param beneficiaryId - the beneficiary identifier.
	 * @param request       - the BeneficiaryRequest passed in the request body.
	 * @param actor         - the authenticated user name.
	 *
	 * @return the updated beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist or was deleted
	 */
	BeneficiaryResponse updateBeneficiary(UUID beneficiaryId, BeneficiaryRequest request, String actor);

	/**
	 * Soft-delete a beneficiary: its status becomes DELETED and it disappears
	 * from reads and default searches.
	 *
	 * @param beneficiaryId - the beneficiary identifier.
	 * @param actor         - the authenticated user name.
	 *
	 * @throws LedgerException NOT_FOUND if it does not exist or was already deleted
	 */
	void deleteBeneficiary(UUID beneficiaryId, String actor);

	/**
	 * Paginated, sortable beneficiary search.
	 *
	 * @param pageable - page, size and sort.
	 * @param filter   - the BeneficiarySearchRequest filters, all optional.
	 * @param actor    - the authenticated user name.
	 *
	 * @return a page of BeneficiaryResponse of type PagedResponse.
	 */
	PagedResponse<BeneficiaryResponse> searchBeneficiaries(Pageable pageable, BeneficiarySearchRequest filter,
			String actor);

}
