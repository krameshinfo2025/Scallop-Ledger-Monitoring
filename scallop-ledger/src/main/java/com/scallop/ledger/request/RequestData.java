/**
 ***********************************************************************************
 RequestData.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scallop.ledger.constant.HolderStatus;
import com.scallop.ledger.constant.HolderType;
import com.scallop.ledger.constant.PostingType;
import com.scallop.ledger.constant.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 
 */
public class RequestData {

	/** Staff sign-in body. */
	public record LoginRequest(@NotBlank @Size(max = 100) String username, @NotBlank @Size(max = 200) String password) {
	}

	/**
	 * BCrypt only reads the first 72 bytes, so longer passwords are refused rather
	 * than silently truncated.
	 */
	public record CreateUserRequest(
			@NotBlank @Pattern(regexp = "^[A-Za-z0-9._-]{3,64}$", message = "3-64 characters: letters, digits, dot, underscore, hyphen") String username,
			@NotBlank @Size(min = 12, max = 72) String password, @NotNull Role role) {
	}

	/** Both fields optional; only the ones present are changed. */
	public record UpdateUserRequest(Boolean enabled, Role role) {
	}

	public record ResetPasswordRequest(@NotBlank @Size(min = 12, max = 72) String newPassword) {
	}

	public record UpdateCurrencyRequest(Boolean enabled) {
	}

	public record CreateLedgerEntitiyRequest(String entityId, String entityName) {
	}

	public record Quote(String quoteId, String entityId, String baseCurrency, String targetCurrency, String rate,
			Instant observedAt, Instant expiresAt, String sourceReference) {
	}

	public record Observation(String account, String currency, String statementReference, Instant observedAt,
			BigDecimal externalBalance, String entityId) {
	}

	public record OpenWalletRequest(String entityId, String customerId, String accountNumber, String holderId,
			HolderType holderType, String currency, String displayName, String businessRegNo, String role,
			Boolean isInitalFunding) {
	}

	public record WalletListRequest(UUID walletId, UUID holderId, String currency) {

	}

	/**
	 * Business event, not client-supplied posting legs. Monetary JSON values should
	 * be decimal strings.
	 */
	public record JournalRequest(@Size(max = 120) String orderViewId, @Size(max = 120) String transactionId,
			@Size(max = 120) String txId, @NotBlank @Pattern(regexp = "[A-Za-z0-9._@-]{1,100}") String userId,
			@Pattern(regexp = "[A-Za-z0-9._@-]{1,100}") String counterpartyUserId,
			@NotBlank @Pattern(regexp = "VOYAGER|MARINER|ADMIRAL|INSTITUTIONS") String tierProfile,
			@NotBlank String transactionType, @NotBlank String status,
			@NotBlank @Pattern(regexp = "[A-Z0-9]{2,16}") String baseCurrency,
			@Pattern(regexp = "[A-Z0-9]{2,16}") String targetCurrency,
			@Size(max = 32) @Pattern(regexp = "[A-Z0-9_-]*") String network, @NotNull BigDecimal grossAmount,
			BigDecimal feeAmount,
			BigDecimal gasAmount, 
			BigDecimal expectedTargetAmount,
			@Size(max = 100) String quoteId, @Size(max = 255) String sourceRailAddress,
			@Size(max = 255) String counterpartyEndpoint, @NotNull Instant occurredAt,
			@Size(max = 255) String originalJournalId) {
	}

	public record PostingLeg(String account, String currency, PostingType type,
			@JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount, String description) {

		public PostingLeg reverse() {
			return new PostingLeg(account, currency, type == PostingType.DR ? PostingType.CR : PostingType.DR, amount,
					"Reversal: " + description);
		}
	}

	public record AccountHolderListRequest(String entityId, String customerId, String accountNumber,
			HolderType holderType, HolderStatus status) {

	}

	public record LedgerEventListRequest(String eventType, UUID eventReferenceId, UUID userId) {

	}


}
