/**
 ***********************************************************************************
 LedgerAssetServiceImpl.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.AppUser;
import com.scallop.ledger.domain.LedgerAsset;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.AppUserRepository;
import com.scallop.ledger.repo.LedgerAssetRepository;
import com.scallop.ledger.request.RequestData.UpdateCurrencyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.CurrencyResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.LedgerAssetService;

/**
 * LedgerAssetServiceImpl is a class act as business logic implementation
 * for Ledger Asset ( Currency) Operations
 * 
 */
@Service(value = "ledgerAssetService")
public class LedgerAssetServiceImpl implements LedgerAssetService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;
	
	/** The Place Holder for appUserRepository of type AppUserRepository */
	private final AppUserRepository appUserRepository;

	/** The Place Holder for supportCurrencyRepository of type SupportCurrencyRepository */
	private final LedgerAssetRepository supportCurrencyRepository;
	
	
	
	/**
	 * @param auditService
	 * @param appUserService
	 * @param ledgerAssetRepository
	 */
	public LedgerAssetServiceImpl( AuditService auditService, 
			                           AppUserRepository appUserRepository,
			                           LedgerAssetRepository supportCurrencyRepository) {
		this.auditService = auditService;
		this.appUserRepository = appUserRepository;
		this.supportCurrencyRepository = supportCurrencyRepository;
	}

	/**
 	 * Method for Fetch All Active Currencies in the System.
 	 * 
 	 * @param pageable - user input for Fetch all Active Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
	@Transactional(readOnly = true)
	public PagedResponse<CurrencyResponse> getActiveCurrencyList(Pageable pageable, String actor){
		
		Page<LedgerAsset> ledgerAssetDataList = supportCurrencyRepository.findByActiveTrue(pageable);

		List<LedgerAsset> ledgerAssetList  = ledgerAssetDataList.getContent();

		List<CurrencyResponse> currencyResponseList = ledgerAssetList.stream()
	            .map(source -> new CurrencyResponse(
	            		source.getCurrency(),
	            		source.getDisplayName(),
	            		source.getActive(),
	            		source.getDisplayScale(),
	            		source.getKind()))
	            .toList(); 

		PagedResponse<CurrencyResponse> response = new PagedResponse<CurrencyResponse>(currencyResponseList,
				pageable.getPageNumber() + 1, ledgerAssetList.size(), ledgerAssetDataList.getTotalElements(),
				ledgerAssetDataList.getTotalPages(), ledgerAssetDataList.hasNext());

		return response;

	}

 	/**
  	 * Method for Fetch All Currencies in the System.
  	 * 
 	 * @param pageable - user input for Fetch all Currencies of type Pageable.
 	 * @param authentication - the authentication of type Authentication passed in the request header.
 	 *
  	 * @return data containing the CurrencyResponse List of type PagedResponse.CurrencyResponse.
 	 * @throws LedgerException if any 
 	 */
	@Transactional(readOnly = true)
	public PagedResponse<CurrencyResponse> getAllCurrencyList(Pageable pageable, String actor){
		
		Page<LedgerAsset> ledgerAssetDataList = supportCurrencyRepository.findAll(pageable);

		List<LedgerAsset> ledgerAssetList  = ledgerAssetDataList.getContent();

		List<CurrencyResponse> currencyResponseList = ledgerAssetList.stream()
		            .map(source -> new CurrencyResponse(
		            		source.getCurrency(),
		            		source.getDisplayName(),
		            		source.getActive(),
		            		source.getDisplayScale(),
		            		source.getKind()))
		            .toList(); 

		PagedResponse<CurrencyResponse> response = new PagedResponse<CurrencyResponse>(currencyResponseList,
				pageable.getPageNumber() + 1, ledgerAssetList.size(), ledgerAssetDataList.getTotalElements(),
				ledgerAssetDataList.getTotalPages(), ledgerAssetDataList.hasNext());

		return response;
	}

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
	public CurrencyResponse updateStatus(String currency, UpdateCurrencyRequest updateCurrencyRequest, String actor){
		
		LedgerAsset ledgerAsset = null;
		
		CurrencyResponse currencyResponse = null;
		
		AppUser currentUser = appUserRepository.findById(UUID.fromString(actor)).get();
		
		Optional<LedgerAsset> ledgerAssetData = supportCurrencyRepository.findById(currency);
		
		if (ledgerAssetData.isPresent()) {
			
			ledgerAsset = ledgerAssetData.get();
			boolean enable = ledgerAsset.getActive();
			
			if (enable == updateCurrencyRequest.enabled()) {

				if (Boolean.FALSE.equals(enable)) {
					throw LedgerException.conflict("CONFLICT", "Currency is already inactive currency.");
				} else {
					throw LedgerException.conflict("CONFLICT", "Currency is already active currency.");
				}

			}else {
				
				StringBuilder changes = new StringBuilder();
				changes.append("enabled=").append(updateCurrencyRequest.enabled()).append(' ');

				ledgerAsset.setActive(updateCurrencyRequest.enabled());
				ledgerAsset.setUpdatedAt(Instant.now());
				ledgerAsset.setUpdatedBy(actor);
				
					
				ledgerAsset = supportCurrencyRepository.saveAndFlush(ledgerAsset);
				
				if (!changes.isEmpty()) {
					auditService.recordIndependently(actor, "USER_UPDATED", "app_user", currentUser.getId(),
							changes.toString().trim());
				}
				
				currencyResponse = new CurrencyResponse(ledgerAsset.getCurrency(),
						                                ledgerAsset.getDisplayName(),
						                                ledgerAsset.getActive(),
						                                ledgerAsset.getDisplayScale(),
						                                ledgerAsset.getKind());
			}
			
		}
		
		return currencyResponse;
	}


}
