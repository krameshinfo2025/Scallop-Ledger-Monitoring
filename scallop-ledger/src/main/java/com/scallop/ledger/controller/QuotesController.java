/**
 ***********************************************************************************
 QuotesController.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.controller;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.Quote;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.QuoteResponse;
import com.scallop.ledger.service.QuotesService;

/**
 * Controller for managing Fx Quotes operations. This includes retrieving Quotes
 * information, and add Quotes information.
 * 
 * @author Ramesh Kaliindi
 */
@RestController
@RequestMapping("/api/v1")
public class QuotesController implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for quotesService of type QuotesService */
	private final QuotesService quotesService;

	/**
	 * QuotesController Constructor
	 * 
	 * @param quotesService
	 */
	public QuotesController(final QuotesService quotesService) {
		this.quotesService = quotesService;
	}

	/**
	 * Method for fetch all Quotes.
	 *
	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any
	 */
	@GetMapping("/quotes")
	public PagedResponse<QuoteResponse> quotes(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, 
			@RequestParam(defaultValue = "observedAt") String sortBy,
			@RequestParam(defaultValue = "true") boolean ascending, 
			final Authentication authentication) {

		Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		// fetch the Quotes from the service layer by invoking getQuotes on
		// quotesService
		PagedResponse<QuoteResponse> data = quotesService.getQuotes(pageable,authentication.getName());

		return data;
	}

	/**
	 * Method for save Quote details in the System.
	 * 
	 * @param quote          - user input for Quote of type RequestData.Quote.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 *
	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any
	 */
	@PostMapping("/quotes")
	public QuoteResponse quote(@RequestBody final Quote quote, final Authentication authentication)
			throws LedgerException {

		// save the Quotes by invoking saveQuote on quotesService
		QuoteResponse data = quotesService.saveQuote(quote, authentication.getName());

		return data;

	}

}
