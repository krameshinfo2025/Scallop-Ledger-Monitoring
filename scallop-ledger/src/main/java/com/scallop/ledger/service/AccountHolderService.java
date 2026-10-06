/**
 ***********************************************************************************
 AccountHolderService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.AccountHolderListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.AccountHolderResponse;


/**
 * AccountHolderService is an interface act as business logic implementation
 * for AccountHolder Operations
 * 
 */
public interface AccountHolderService {

  	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param accountHolderListRequest - containing the AccountHolderListRequest List of type RequestData.AccountHolderListRequest.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the AccountHolderResponse List of type PagedResponse.AccountHolderResponse.
  	 * @throws LedgerException if any 
  	 */
	public PagedResponse<AccountHolderResponse> getAllAccountHolders(Pageable pageable,
			AccountHolderListRequest accountHolderListRequest, String actor);

}
