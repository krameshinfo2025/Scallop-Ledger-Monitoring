/**
 ***********************************************************************************
 BeneficiaryController.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryRequest;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryResponse;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiarySearchRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.service.BeneficiaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Beneficiary CRUD (delete is soft) and a paginated, sortable search.
 */
@RestController
@RequestMapping("/api/v1/beneficiaries")
@Tag(name = "Beneficiary Management")
public class BeneficiaryController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final int MAX_PAGE_SIZE = 100;

	private static final BeneficiarySearchRequest NO_FILTER = new BeneficiarySearchRequest(null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null);

	/** API sort keys mapped to entity paths; anything else is rejected rather than failing deep in JPA. */
	private static final Map<String, String> SORTABLE = Map.ofEntries(
			Map.entry("createdAt", "createdAt"),
			Map.entry("updatedAt", "updatedAt"),
			Map.entry("beneficiaryType", "beneficiaryType"),
			Map.entry("currencyCode", "currencyCode"),
			Map.entry("status", "status"),
			Map.entry("firstName", "accountHolder.firstName"),
			Map.entry("lastName", "accountHolder.lastName"),
			Map.entry("businessName", "accountHolder.businessName"),
			Map.entry("bankName", "bank.name"));

	/** The Place Holder for beneficiaryService of type BeneficiaryService */
	private final BeneficiaryService beneficiaryService;

	/**
	 * @param beneficiaryService
	 */
	public BeneficiaryController(BeneficiaryService beneficiaryService) {
		super();
		this.beneficiaryService = beneficiaryService;
	}

	/**
	 * Create a beneficiary.
	 *
	 * @param request        - the BeneficiaryRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the created beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException if any
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a beneficiary")
	public BeneficiaryResponse create(@Valid @RequestBody final BeneficiaryRequest request,
			final Authentication authentication) {

		return beneficiaryService.createBeneficiary(request, authentication.getName());
	}

	/**
	 * Fetch one beneficiary.
	 *
	 * @param beneficiaryId  - the beneficiary identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist or was deleted
	 */
	@GetMapping("/{beneficiaryId}")
	@Operation(summary = "Get a beneficiary")
	public BeneficiaryResponse get(@PathVariable final UUID beneficiaryId, final Authentication authentication) {

		return beneficiaryService.getBeneficiary(beneficiaryId, authentication.getName());
	}

	/**
	 * Replace a beneficiary.
	 *
	 * @param beneficiaryId  - the beneficiary identifier passed in the request path.
	 * @param request        - the BeneficiaryRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the updated beneficiary of type BeneficiaryResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist or was deleted
	 */
	@PostMapping("/{beneficiaryId}")
	@Operation(summary = "Replace a beneficiary")
	public BeneficiaryResponse update(@PathVariable final UUID beneficiaryId,
			@Valid @RequestBody final BeneficiaryRequest request, final Authentication authentication) {

		return beneficiaryService.updateBeneficiary(beneficiaryId, request, authentication.getName());
	}

	/**
	 * Soft-delete a beneficiary.
	 *
	 * @param beneficiaryId  - the beneficiary identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @throws LedgerException NOT_FOUND if it does not exist or was already deleted
	 */
	@DeleteMapping("/{beneficiaryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete a beneficiary (soft delete)")
	public void delete(@PathVariable final UUID beneficiaryId, final Authentication authentication) {

		beneficiaryService.deleteBeneficiary(beneficiaryId, authentication.getName());
	}

	/**
	 * Paginated beneficiary search. All filters are optional; an empty body lists
	 * every non-deleted beneficiary.
	 *
	 * @param page           - zero-based page number.
	 * @param size           - page size, 1 to 100.
	 * @param sortBy         - one of the SORTABLE keys.
	 * @param ascending      - sort direction.
	 * @param filter         - the BeneficiarySearchRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return data containing the BeneficiaryResponse List of type PagedResponse.
	 * @throws LedgerException if the paging or sort parameters are invalid
	 */
	@PostMapping("/search")
	@Operation(summary = "Search beneficiaries with pagination")
	public PagedResponse<BeneficiaryResponse> search(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "false") boolean ascending,
			@Valid @RequestBody(required = false) BeneficiarySearchRequest filter,
			final Authentication authentication) {

		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			throw LedgerException.badRequest("INVALID_PAGE",
					"page must be >= 0 and size between 1 and " + MAX_PAGE_SIZE + ".");
		}
		String sortPath = SORTABLE.get(sortBy);
		if (sortPath == null) {
			throw LedgerException.badRequest("INVALID_SORT", "sortBy must be one of " + SORTABLE.keySet() + ".");
		}

		Sort sort = ascending ? Sort.by(sortPath).ascending() : Sort.by(sortPath).descending();
		// Tie-break on id so rows with equal sort keys never repeat or vanish across pages.
		Pageable pageable = PageRequest.of(page, size, sort.and(Sort.by("id")));

		return beneficiaryService.searchBeneficiaries(pageable, filter == null ? NO_FILTER : filter,
				authentication.getName());
	}

}
