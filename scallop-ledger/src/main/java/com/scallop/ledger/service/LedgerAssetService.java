/**
 ***********************************************************************************
 LedgerAssetService.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.UpdateCurrencyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.CurrencyResponse;

/**
 * LedgerAssetService is an interface act as business logic implementation
 * for Ledger Asset ( Currency) Operations
 * 
 */
public interface LedgerAssetService {

	/**
 	 * Method for Fetch All Active Currencies in the System.
 	 * 
 	 * @param pageable - user input for Fetch all Active Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
	public PagedResponse<CurrencyResponse> getActiveCurrencyList(Pageable pageable, String actor);

 	/**
  	 * Method for Fetch All Currencies in the System.
  	 * 
 	 * @param pageable - user input for Fetch all Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
	public PagedResponse<CurrencyResponse> getAllCurrencyList(Pageable pageable, String actor);

  	/**
	 * Method for Enable/disable a currency in the System.
	 * 
	 * @param currency - a path variable and currency identifier of type String.
	 * @param updateCurrencyRequest- user input for Update Currency of type RequestData.UpdateCurrencyRequest.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the CurrencyResponse of type ResponseData.CurrencyResponse.
	 * @throws LedgerException if any 
	 */
	public CurrencyResponse updateStatus(String currency, UpdateCurrencyRequest updateCurrencyRequest, String actor);

}
