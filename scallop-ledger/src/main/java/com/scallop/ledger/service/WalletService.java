/**
 ***********************************************************************************
 WalletService.Java
 version 1.0
 25,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service;


import org.springframework.data.domain.Pageable;

import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.request.RequestData.OpenWalletRequest;
import com.scallop.ledger.request.RequestData.WalletListRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.WalletResponse;

/**
 * WalletService is an interface for all Wallet Operations
 * 
 */
public interface WalletService {

	/**
	 * Request Open a wallet in one currency, with its ledger account.
	 *
	 * @param request        - the OpenWalletRequest passed in the request body.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 * 
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
	public WalletResponse openWallet(OpenWalletRequest openWalletRequest, String actor);

	/**
	 * Request fetch all wallet of an account holder
	 *
	 * @param holderId       - the user identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
	//List<WalletResponse> listWallets(final String holderId, String actor);

	/**
	 * Request fetch a wallet details based on wallet id
	 *
	 * @param walletId       - the wallet identifier passed in the request path.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 * 
	 * @return data containing the Historical Balances from the service layer.
	 * @throws LedgerException if any
	 */
	//WalletResponse getWallet(String walletId, String actor);

	
 	/**
  	 * Method for Fetch All Wallets in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param walletListRequest - containing the WalletListRequest List of type RequestData.WalletListRequest.
  	 * @param authentication - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the WalletResponse List of type PagedResponse.WalletResponse.
  	 * @throws LedgerException if any 
  	 */
	public PagedResponse<WalletResponse> getAllWallets(Pageable pageable, WalletListRequest walletListRequest,
			String actor);

}
