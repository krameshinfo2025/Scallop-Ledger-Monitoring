/**
 ***********************************************************************************
 ResponseData.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.AcountHolderAccountType;
import com.scallop.ledger.constant.AssetKind;
import com.scallop.ledger.constant.HolderStatus;
import com.scallop.ledger.constant.HolderType;
import com.scallop.ledger.constant.Role;
import com.scallop.ledger.constant.WalletStatus;
import com.scallop.ledger.domain.AppUser;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 */
public class ResponseData {

	/** Staff sign-in body. */
	public record LoginResponse(
			String accessToken,
			@Schema(example = "Bearer") String tokenType,
			@Schema(description = "Access token lifetime in seconds") long expiresIn,
			String username, 
			Role role) {	
	}

	public record UserResponse(UUID id, String username, Role role, boolean enabled) {
		public static UserResponse of(AppUser appUser) {
			return new UserResponse(appUser.getId(), appUser.getUsername(), appUser.getRole(), appUser.isEnabled());
		}
	}
	
	public record CurrencyResponse (String currency,String displayName,Boolean active,Short displayScale,AssetKind kind) {		
	}
	
	public record LedgerEntiryResponse(String entityId,String entityName) {}
	
	public record QuoteResponse(String quoteId,
			String entityId,
			String baseCurrency,
			String targetCurrency,
			String rate,
			Instant observedAt, 
			Instant expiresAt,
			String sourceReference) {}
	
	public record ReconciliationResponse(UUID observationId,String ledgerBalance,String difference,boolean matched) {}

	public record AccountHolderResponse (UUID id,String entityId,String customerId,
			String accountNumber,HolderType holderType,AcountHolderAccountType accountType,
			String displayName,String businessRegNo,String role,
			HolderStatus status) {
		
		
	}
	
	public record WalletResponse (UUID walletId,UUID holderId,String currency,Short displayScale,AssetKind kind,
			WalletStatus status,BigDecimal balance) {
		
	}
	
	public record LedgerEventResponse() {
		
	}
	
	
}
