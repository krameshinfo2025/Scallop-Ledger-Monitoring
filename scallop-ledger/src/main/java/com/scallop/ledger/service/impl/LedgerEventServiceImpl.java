/**
 * 
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.scallop.ledger.request.RequestData.LedgerEventListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEventResponse;
import com.scallop.ledger.service.LedgerEventService;

/**
 * 
 */
@Service(value = "ledgerEventService")
public class LedgerEventServiceImpl implements LedgerEventService,Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;	

	public PagedResponse<LedgerEventResponse> getAllLedgerEvents(Pageable pageable,
			LedgerEventListRequest ledgerEventListRequest, String name){
		return null;
	}




}
