/**
 ***********************************************************************************
 LedgerEntitiyService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.CreateLedgerEntitiyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEntiryResponse;

/**
 * LedgerEntitiyService is an interface act as business logic implementation
 * for App System User Operations
 * 
 */
public interface LedgerEntityService {

	/**
	 * Method for create New Ledger Entity in the System.
	 * 
	 * @param createLedgerEntitiyRequest - user input for create Ledger Entity of type RequestData.CreateLedgerEntitiyRequest.
	 * @param actor - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any 
	 */
	public LedgerEntiryResponse create(CreateLedgerEntitiyRequest createLedgerEntitiyRequest, String actor);

	
  	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param actor - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the LedgerEntiryResponse List of type PagedResponse.LedgerEntiryResponse.
  	 * @throws LedgerException if any 
  	 */
	public PagedResponse<LedgerEntiryResponse> getAllLedgerEntities(Pageable pageable, String actor);

}
