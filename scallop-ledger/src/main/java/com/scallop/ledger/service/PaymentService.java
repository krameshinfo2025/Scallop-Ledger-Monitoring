/**
 ***********************************************************************************
 PaymentService.Java
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
import com.scallop.ledger.request.PaymentRequestData.PaymentRequest;
import com.scallop.ledger.request.PaymentRequestData.PaymentResponse;
import com.scallop.ledger.request.PaymentRequestData.PaymentSearchRequest;
import com.scallop.ledger.response.PagedResponse;

/**
 * PaymentService is an interface act as business logic implementation
 * for Payment Operations. Payments are records only - nothing here posts to
 * the ledger or moves wallet balances.
 *
 */
public interface PaymentService {

	/**
	 * Create a payment with its payer, payee, amounts and optional quote.
	 *
	 * @param request - the PaymentRequest passed in the request body.
	 * @param actor   - the authenticated user name.
	 *
	 * @return the created payment of type PaymentResponse.
	 * @throws LedgerException if the request breaks a business rule
	 */
	PaymentResponse createPayment(PaymentRequest request, String actor);

	/**
	 * Fetch one payment, including cancelled ones.
	 *
	 * @param paymentId - the payment identifier.
	 * @param actor     - the authenticated user name.
	 *
	 * @return the payment of type PaymentResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist
	 */
	PaymentResponse getPayment(UUID paymentId, String actor);

	/**
	 * Replace every field of a payment (PUT semantics).
	 *
	 * @param paymentId - the payment identifier.
	 * @param request   - the PaymentRequest passed in the request body.
	 * @param actor     - the authenticated user name.
	 *
	 * @return the updated payment of type PaymentResponse.
	 * @throws LedgerException NOT_FOUND if it does not exist, CONFLICT if it is
	 *                         settled, locked or cancelled
	 */
	PaymentResponse updatePayment(UUID paymentId, PaymentRequest request, String actor);

	/**
	 * Soft-delete a payment: its status becomes CANCELLED and it disappears from
	 * default searches.
	 *
	 * @param paymentId - the payment identifier.
	 * @param actor     - the authenticated user name.
	 *
	 * @throws LedgerException NOT_FOUND if it does not exist, CONFLICT if it is
	 *                         settled, locked or already cancelled
	 */
	void deletePayment(UUID paymentId, String actor);

	/**
	 * Paginated, sortable payment search.
	 *
	 * @param pageable - page, size and sort.
	 * @param filter   - the PaymentSearchRequest filters, all optional.
	 * @param actor    - the authenticated user name.
	 *
	 * @return a page of PaymentResponse of type PagedResponse.
	 */
	PagedResponse<PaymentResponse> searchPayments(Pageable pageable, PaymentSearchRequest filter, String actor);

}
