/**
 ***********************************************************************************
 PaymentController.Java
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.PaymentRequestData.PaymentRequest;
import com.scallop.ledger.request.PaymentRequestData.PaymentResponse;
import com.scallop.ledger.request.PaymentRequestData.PaymentSearchRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Payment CRUD (delete is a soft cancel) and a paginated, sortable search.
 * Payments are records only and do not post to the ledger.
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management")
public class PaymentController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final int MAX_PAGE_SIZE = 100;

	private static final PaymentSearchRequest NO_FILTER = new PaymentSearchRequest(null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	/** API sort keys mapped to entity paths; anything else is rejected rather than failing deep in JPA. */
	private static final Map<String, String> SORTABLE = Map.ofEntries(
			Map.entry("createdAt", "createdAt"),
			Map.entry("updatedAt", "updatedAt"),
			Map.entry("status", "status"),
			Map.entry("transactionType", "transactionType"),
			Map.entry("transferType", "transferType"),
			Map.entry("sourceCurrency", "payer.sourceCurrency"),
			Map.entry("sourceAmount", "payer.sourceAmount"),
			Map.entry("destinationCurrency", "payee.destinationCurrency"),
			Map.entry("beneficiaryName", "payee.beneficiaryName"),
			Map.entry("principleAmount", "amount.principleAmount"),
			Map.entry("totalAmount", "amount.totalAmount"));

	/** The Place Holder for paymentService of type PaymentService */
	private final PaymentService paymentService;

	/**
	 * @param paymentService
	 */
	public PaymentController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}

	/**
	 * Create a payment.
	 *
	 * @param request        - the PaymentRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the created payment of type PaymentResponse.
	 * @throws LedgerException if any
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a payment")
	public PaymentResponse create(@Valid @RequestBody final PaymentRequest request,
			final Authentication authentication) {

		return paymentService.createPayment(request, authentication.getName());
	}

	/**
	 * Fetch one payment. Cancelled payments are returned too.
	 *
	 * @param paymentId      - the payment identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the payment of type PaymentResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist
	 */
	@GetMapping("/{paymentId}")
	@Operation(summary = "Get a payment")
	public PaymentResponse get(@PathVariable final UUID paymentId, final Authentication authentication) {

		return paymentService.getPayment(paymentId, authentication.getName());
	}

	/**
	 * Replace a payment.
	 *
	 * @param paymentId      - the payment identifier passed in the request path.
	 * @param request        - the PaymentRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return the updated payment of type PaymentResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist, CONFLICT if it is settled, locked or cancelled
	 */
	@PutMapping("/{paymentId}")
	@Operation(summary = "Replace a payment (not once settled, locked or cancelled)")
	public PaymentResponse update(@PathVariable final UUID paymentId, @Valid @RequestBody final PaymentRequest request,
			final Authentication authentication) {

		return paymentService.updatePayment(paymentId, request, authentication.getName());
	}

	/**
	 * Cancel a payment (soft delete).
	 *
	 * @param paymentId      - the payment identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @throws LedgerException NOT_FOUND if it does not exist, CONFLICT if it is settled, locked or already cancelled
	 */
	@DeleteMapping("/{paymentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Cancel a payment (soft delete)")
	public void delete(@PathVariable final UUID paymentId, final Authentication authentication) {

		paymentService.deletePayment(paymentId, authentication.getName());
	}

	/**
	 * Paginated payment search. All filters are optional; an empty body lists
	 * every payment that is not cancelled.
	 *
	 * @param page           - zero-based page number.
	 * @param size           - page size, 1 to 100.
	 * @param sortBy         - one of the SORTABLE keys.
	 * @param ascending      - sort direction.
	 * @param filter         - the PaymentSearchRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
	 * @return data containing the PaymentResponse List of type PagedResponse.
	 * @throws LedgerException if the paging, sort or range parameters are invalid
	 */
	@PostMapping("/search")
	@Operation(summary = "Search payments with pagination")
	public PagedResponse<PaymentResponse> search(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "false") boolean ascending,
			@Valid @RequestBody(required = false) PaymentSearchRequest filter,
			final Authentication authentication) {

		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			throw LedgerException.badRequest("INVALID_PAGE",
					"page must be >= 0 and size between 1 and " + MAX_PAGE_SIZE + ".");
		}
		String sortPath = SORTABLE.get(sortBy);
		if (sortPath == null) {
			throw LedgerException.badRequest("INVALID_SORT", "sortBy must be one of " + SORTABLE.keySet() + ".");
		}
		PaymentSearchRequest effective = filter == null ? NO_FILTER : filter;
		if (effective.minTotalAmount() != null && effective.maxTotalAmount() != null
				&& effective.minTotalAmount().compareTo(effective.maxTotalAmount()) > 0) {
			throw LedgerException.badRequest("INVALID_RANGE", "minTotalAmount cannot be greater than maxTotalAmount.");
		}
		if (effective.createdFrom() != null && effective.createdTo() != null
				&& effective.createdFrom().isAfter(effective.createdTo())) {
			throw LedgerException.badRequest("INVALID_RANGE", "createdFrom cannot be after createdTo.");
		}

		Sort sort = ascending ? Sort.by(sortPath).ascending() : Sort.by(sortPath).descending();
		// Tie-break on id so rows with equal sort keys never repeat or vanish across pages.
		Pageable pageable = PageRequest.of(page, size, sort.and(Sort.by("id")));

		return paymentService.searchPayments(pageable, effective, authentication.getName());
	}

}
