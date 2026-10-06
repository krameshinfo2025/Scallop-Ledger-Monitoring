/**
 ***********************************************************************************
 WalletService.Java
 version 1.0
 25,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.constant.AcountHolderAccountType;
import com.scallop.ledger.constant.HolderStatus;
import com.scallop.ledger.constant.WalletStatus;
import com.scallop.ledger.domain.AccountHolder;
import com.scallop.ledger.domain.LedgerAsset;
import com.scallop.ledger.domain.Wallet;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.AccountHolderRepository;
import com.scallop.ledger.repo.LedgerAssetRepository;
import com.scallop.ledger.repo.WalletRepository;
import com.scallop.ledger.request.RequestData.OpenWalletRequest;
import com.scallop.ledger.request.RequestData.WalletListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.WalletResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.WalletService;

/**
 * WalletService is a class for all Wallet Operations
 * 
 */
@Service(value = "walletService")
public class WalletServiceImpl implements WalletService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;


	/**
	 * The Place Holder for accountHolderRepository of type AccountHolderRepository
	 */
	private final AccountHolderRepository accountHolderRepository;

	/**
	 * The Place Holder for ledgerAssetRepository of type LedgerAssetRepository
	 */
	private final LedgerAssetRepository ledgerAssetRepository;

	/**
	 * The Place Holder for walletRepository of type WalletRepository
	 */
	private final WalletRepository walletRepository;

	/**
	 * @param accountHolderRepository
	 * @param walletRepository
	 */
	public WalletServiceImpl(AuditService auditService,AccountHolderRepository accountHolderRepository,
			LedgerAssetRepository ledgerAssetRepository, WalletRepository walletRepository) {
		this.auditService = auditService;
		this.accountHolderRepository = accountHolderRepository;
		this.ledgerAssetRepository = ledgerAssetRepository;
		this.walletRepository = walletRepository;
	}

	/**
	 * Method for Fetch All Wallets in the System.
	 * 
	 * @param pageable          - user input for Fetch all Ledger Entities of type
	 *                          Pageable.
	 * @param walletListRequest - containing the WalletListRequest List of type
	 *                          RequestData.WalletListRequest.
	 * @param authentication    - the authentication of type Authentication passed
	 *                          in the request header.
	 *
	 * @return data containing the WalletResponse List of type
	 *         PagedResponse.WalletResponse.
	 * @throws LedgerException if any
	 */
	@Transactional(readOnly = true)
	public PagedResponse<WalletResponse> getAllWallets(Pageable pageable, WalletListRequest walletListRequest,
			String actor) {

		Page<Wallet> dataList = null;
		UUID walletId = walletListRequest.walletId();
		UUID holderId = walletListRequest.holderId();
		String currency = walletListRequest.currency();

		dataList = walletRepository.findWallets(pageable, walletId, holderId, currency);

		List<Wallet> walletList = dataList.getContent();

		List<WalletResponse> accountHolderResponseList = walletList.stream()
				.map(wallet -> new WalletResponse(wallet.getId(), wallet.getHolder(), wallet.getCurrency(),
						wallet.getDisplayScale(), wallet.getKind(), wallet.getStatus(),
						wallet.getBalance().movePointLeft(wallet.getDisplayScale())))
				.toList();

		PagedResponse<WalletResponse> response = new PagedResponse<WalletResponse>(accountHolderResponseList,
				pageable.getPageNumber() + 1, dataList.getContent().size(), dataList.getTotalElements(),
				dataList.getTotalPages(), dataList.hasNext());

		return response;
	}

	/**
	 * Request Open a wallet in one currency, with its ledger account.
	 *
	 * @param holderId       - the user identifier passed in the request path.
	 * @param request        - the OpenWalletRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 * 
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
	@Transactional
	public WalletResponse openWallet(OpenWalletRequest openWalletRequest, String actor) {

		WalletResponse walletResponse = null;

		Wallet wallet = null;
		
		BigDecimal balance = BigDecimal.ZERO;
		
		LedgerAsset ledgerAsset = ledgerAssetRepository.findById(openWalletRequest.currency()).get();
		
		if(ledgerAsset.getActive() == Boolean.FALSE) {
			
			throw LedgerException.conflict("Currency is Supported by the System.");
		}
		if(openWalletRequest.isInitalFunding() == Boolean.TRUE) {
			
			balance = BigDecimal.valueOf(10);
		}

		Optional<AccountHolder> existingAccountHolder = accountHolderRepository.findAccountHolder(
				openWalletRequest.entityId(), openWalletRequest.customerId(), openWalletRequest.currency(),openWalletRequest.holderType().toString());

		if (existingAccountHolder.isPresent()) {

			AccountHolder accountHolder = existingAccountHolder.get();

			wallet = new Wallet(accountHolder.getId(),ledgerAsset.getCurrency(),
					ledgerAsset.getDisplayScale(),ledgerAsset.getKind(),WalletStatus.ACTIVE,balance);

			wallet = walletRepository.saveAndFlush(wallet);
			
			auditService.recordIndependently(actor, "WALLET_CREATED", openWalletRequest.entityId(), wallet.getId(),
					accountHolder.getDisplayName() + " : " +  wallet.getId());


		} else {
			
			AccountHolder accountHolder = new AccountHolder(
					openWalletRequest.entityId(), openWalletRequest.customerId(), 
					openWalletRequest.currency(),openWalletRequest.holderType(),AcountHolderAccountType.MULTICURRENCY,
					openWalletRequest.displayName(),openWalletRequest.businessRegNo(),openWalletRequest.role(),
					HolderStatus.ACTIVE,Instant.now(),Instant.now(),actor,actor);
			
			accountHolder = accountHolderRepository.save(accountHolder);
			
			auditService.recordIndependently(actor, "ACCOUNT_HOLDER_CREATED", openWalletRequest.entityId(), accountHolder.getId(),
					accountHolder.getAccountNumber() + " as " + accountHolder.getDisplayName());
			
			wallet = new Wallet(accountHolder.getId(),ledgerAsset.getCurrency(),
					ledgerAsset.getDisplayScale(),ledgerAsset.getKind(),WalletStatus.ACTIVE,balance,Instant.now(),Instant.now(),actor,actor);
			
			auditService.recordIndependently(actor, "WALLET_CREATED", openWalletRequest.entityId(), wallet.getId(),
					accountHolder.getDisplayName() + " : " +  wallet.getId());


			wallet = walletRepository.save(wallet);


		}

		walletResponse = new WalletResponse(wallet.getId(), wallet.getHolder(), wallet.getCurrency(),
				wallet.getDisplayScale(), wallet.getKind(), wallet.getStatus(),
				wallet.getBalance().movePointLeft(wallet.getDisplayScale()));

		return walletResponse;
	}

}
