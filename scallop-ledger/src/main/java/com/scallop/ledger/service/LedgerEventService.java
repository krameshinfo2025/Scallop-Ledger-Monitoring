/**
 * 
 */
package com.scallop.ledger.service;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.request.RequestData.LedgerEventListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEventResponse;

/**
 * 
 */
public interface LedgerEventService {

	PagedResponse<LedgerEventResponse> getAllLedgerEvents(Pageable pageable,
			LedgerEventListRequest ledgerEventListRequest, String name);

}
