/**
 ***********************************************************************************
 QuotesService.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;

import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.Quote;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.QuoteResponse;

/**
 * QuotesService is an interface for fetch Quotes 
 * of all the users.
 * 
 */
public interface QuotesService {

	

	/**
	 * Method for fetch all Quotes.
	 *
 	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any 
	 */
	public PagedResponse<QuoteResponse> getQuotes(Pageable pageable, String actor);

	

	/**
	 * Method for save Quote details in the System.
	 * 
	 * @param quote - user input for Quote of type QuoteRequest.Quote.
	 * @param authentication - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any 
	 */
	public QuoteResponse saveQuote(Quote quote, String actor);
	

}
