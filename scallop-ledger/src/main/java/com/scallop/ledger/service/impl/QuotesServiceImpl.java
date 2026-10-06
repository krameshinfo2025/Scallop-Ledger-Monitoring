/**
 ***********************************************************************************
 QuotesServiceImpl.Java
 version 1.0
 24,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.FxQuote;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.FxQuoteRepository;
import com.scallop.ledger.request.RequestData.Quote;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.QuoteResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.QuotesService;

/**
 * QuotesServiceImpl is a Class for fetch Quotes of all the users.
 *
 */
@Service(value = "quotesService")
public class QuotesServiceImpl implements QuotesService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/** The Place Holder for fxQuoteRepository of type FxQuoteRepository */
	private final FxQuoteRepository fxQuoteRepository;

	/**
	 * @param auditService
	 * @param fxQuoteRepository
	 */
	public QuotesServiceImpl(final AuditService auditService, final FxQuoteRepository fxQuoteRepository) {
		this.auditService = auditService;
		this.fxQuoteRepository = fxQuoteRepository;
	}

	/**
	 * Method for fetch all Quotes.
	 *
	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any
	 */
	@Override
	@Transactional(readOnly = true)
	public PagedResponse<QuoteResponse> getQuotes(Pageable pageable, String actor)  {
		
		Page<FxQuote> data = fxQuoteRepository.findAll(pageable);

		List<FxQuote> fxQuoteList = data.getContent();

	
		List<QuoteResponse> quoteResponseList = fxQuoteList.stream()
	            .map(fxQuote -> new QuoteResponse(fxQuote.getQuoteId(),
	    				fxQuote.getEntityId(),
	    				fxQuote.getBaseCurrency(), 
	    				fxQuote.getTargetCurrency(),
	    				fxQuote.getRate(),
	    				fxQuote.getObservedAt(), 
	    				fxQuote.getExpiresAt(),
	    				fxQuote.getSourceReference()))
	            .toList(); 
		

		PagedResponse<QuoteResponse> response = new PagedResponse<QuoteResponse>(quoteResponseList,
				pageable.getPageNumber() + 1, data.getContent().size(), data.getTotalElements(),
				data.getTotalPages(), data.hasNext());

		return response;

	}

	/**
	 * Method for save Quote details in the System.
	 *
	 * @param quote          - user input for Quote of type QuoteRequest.Quote.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 *
	 * @return data containing the Quotes from the service layer.
	 * @throws LedgerException if any
	 */
	@Override
	@Transactional
	public QuoteResponse saveQuote(final Quote quote, final String actor) throws LedgerException {

		Instant now = Instant.now();

		if (quote.quoteId() == null || !quote.quoteId().matches("[A-Za-z0-9._:-]{1,100}")
				|| quote.sourceReference() == null || quote.sourceReference().isBlank()
				|| quote.sourceReference().length() > 500 || quote.observedAt() == null
				|| quote.expiresAt() == null || quote.observedAt().isAfter(now)
				|| quote.observedAt().isBefore(now.minusSeconds(300))
				|| !quote.expiresAt().isAfter(now)
				|| quote.expiresAt().isAfter(quote.observedAt().plusSeconds(900))) {
			throw new LedgerException
					 (HttpStatus.BAD_REQUEST.value(),"QUOATE_OBSERVED_EXPIRED","Quote must be recently observed, unexpired and valid for at most 15 minutes");
		}
		
		FxQuote fxQuote = new FxQuote(quote.quoteId(),
				                      quote.entityId() ,
                                      quote.baseCurrency(), 
                                      quote.targetCurrency(),
                                      quote.rate(),
                                      quote.observedAt(), 
                                      quote.expiresAt(),
                                      quote.sourceReference(), 
                                      actor);

		fxQuote = fxQuoteRepository.save(fxQuote);

		auditService.recordIndependently(actor, "FX_QUOATE_CREATED", fxQuote.getEntityId(), fxQuote.getId(),
				fxQuote.getQuoteId() + " as " + fxQuote.getId());

		
		QuoteResponse quoteResponse = new QuoteResponse( 
				fxQuote.getQuoteId(),
				fxQuote.getEntityId(),
				fxQuote.getBaseCurrency(), 
				fxQuote.getTargetCurrency(),
				fxQuote.getRate(),
				fxQuote.getObservedAt(), 
				fxQuote.getExpiresAt(),
				fxQuote.getSourceReference());
		
		return quoteResponse;

	}

}
