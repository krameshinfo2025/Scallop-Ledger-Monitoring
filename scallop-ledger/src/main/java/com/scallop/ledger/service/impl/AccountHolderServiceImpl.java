/**
 ***********************************************************************************
 AccountHolderRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.constant.HolderStatus;
import com.scallop.ledger.constant.HolderType;
import com.scallop.ledger.domain.AccountHolder;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.AccountHolderRepository;
import com.scallop.ledger.request.RequestData.AccountHolderListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.AccountHolderResponse;
import com.scallop.ledger.service.AccountHolderService;

/**
 * AccountHolderServiceImpl is a class act as business logic implementation for
 * AccountHolder Operations
 * 
 */
@Service(value = "accountHolderService")
public class AccountHolderServiceImpl implements AccountHolderService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Place Holder for accountHolderRepository of type AccountHolderRepository
	 */
	private final AccountHolderRepository accountHolderRepository;

	/**
	 * @param accountHolderRepository
	 */
	public AccountHolderServiceImpl(AccountHolderRepository accountHolderRepository) {
		super();
		this.accountHolderRepository = accountHolderRepository;
	}

	/**
	 * Method for Fetch All App System User in the System.
	 * 
	 * @param pageable                 - user input for Fetch all Ledger Entities of
	 *                                 type Pageable.
	 * @param accountHolderListRequest - containing the AccountHolderListRequest
	 *                                 List of type
	 *                                 RequestData.AccountHolderListRequest.
	 * @param authentication           - the authentication of type Authentication
	 *                                 passed in the request header.
	 *
	 * @return data containing the AccountHolderResponse List of type
	 *         PagedResponse.AccountHolderResponse.
	 * @throws LedgerException if any
	 */
	@Transactional(readOnly = true)
	public PagedResponse<AccountHolderResponse> getAllAccountHolders(Pageable pageable,
			AccountHolderListRequest accountHolderListRequest, String actor) {

		Page<AccountHolder> dataList = null;
		String entityId = accountHolderListRequest.entityId();
		String customerId = accountHolderListRequest.customerId();
		String accountNumber = accountHolderListRequest.accountNumber();
		HolderType holderType = accountHolderListRequest.holderType();
		HolderStatus status = accountHolderListRequest.status();

		dataList = accountHolderRepository.findAccountHolder(pageable,
				entityId, customerId, accountNumber, holderType.toString(), status.toString());

		List<AccountHolder> accountHolderList = dataList.getContent();

		List<AccountHolderResponse> accountHolderResponseList = accountHolderList.stream()
				.map(accountHolder -> new AccountHolderResponse(accountHolder.getId(), accountHolder.getEntityId(),
						accountHolder.getCustomerId(), accountHolder.getAccountNumber(), accountHolder.getHolderType(),
						accountHolder.getAccountType(), accountHolder.getDisplayName(),
						accountHolder.getBusinessRegNo(), accountHolder.getRole(), accountHolder.getStatus()))
				.toList();

		PagedResponse<AccountHolderResponse> response = new PagedResponse<AccountHolderResponse>(
				accountHolderResponseList, pageable.getPageNumber() + 1, dataList.getContent().size(),
				dataList.getTotalElements(), dataList.getTotalPages(), dataList.hasNext());

		return response;

	}

}
