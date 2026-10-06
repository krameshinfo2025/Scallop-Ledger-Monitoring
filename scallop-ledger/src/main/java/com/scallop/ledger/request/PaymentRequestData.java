/**
 * 
 */
package com.scallop.ledger.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.scallop.ledger.constant.PaymentStatus;
import com.scallop.ledger.request.RequestData.Quote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Request and response shapes for Payment Management. Shape-level checks
 * live here; rules that span fields (account number or IBAN, name by holder
 * type, known currency) are enforced in the service and by the database.
 */
public class PaymentRequestData {
	
	/**
	 * A payment record. {@code quote} is required when the payer and payee
	 * currencies differ and must name an existing fx_quotes row (quoteId +
	 * entityId); only those two fields are read, the rest is rebuilt from the
	 * stored quote. Cross-field and database rules are enforced in the service.
	 */
	public record PaymentRequest(
			Quote quote,
			@NotNull @Valid Payer payer,
			@NotNull @Valid Payee payee,
			@NotNull @Valid TransactionAmount transactionAmount,
			@NotNull @Valid PaymentTransaction paymentTransaction,
			@Valid PaymentAdditionalInformation paymentAdditionalInformation) {

	}

	public record Payer(@NotBlank @Size(max = 100) String userId,
			            @Size(max = 64) String sourceWalletId,
			            @NotBlank @Pattern(regexp = "^[A-Z]{3,10}$", message = "must be an upper-case currency code") String sourceCurrency,
			            @NotNull @Positive BigDecimal sourceAmount,
			            @Size(max = 255) String sourceAddress,
			            @Size(max = 2048) String sourceImage) { }

	public record Payee(@Size(max = 64) String destinationWalletId,
                        @NotBlank @Pattern(regexp = "^[A-Z]{3,10}$", message = "must be an upper-case currency code") String destinationCurrency,
                        @Positive BigDecimal destinationAmount,
                        @Size(max = 255) String destinationAddress,
                        @Size(max = 2048) String destinationImage,
                        Boolean isBeneficiaryInSytem,
                        @Size(max = 64) String beneficiaryId,
                        @Size(max = 140) String beneficiaryName,
                        @Size(max = 40) String accountNumberOrIBAN,
                        @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "must be an 8 or 11 character BIC") String codeSwiftorBIC,
                        @Size(max = 20) String codeRoutingOrLocal,
                        @Size(max = 500) String bankAddress) {}

	public record TransactionAmount(@NotNull @Positive BigDecimal principleAmount,
			@PositiveOrZero BigDecimal feeAmount,
			@PositiveOrZero BigDecimal networkFee,
			@PositiveOrZero BigDecimal otherFee,
			@PositiveOrZero BigDecimal totalFee,
			@PositiveOrZero BigDecimal feeInUSD,
			@PositiveOrZero BigDecimal discount,
			@PositiveOrZero BigDecimal netGrossAmount,
			@NotNull @Positive BigDecimal totalAmount) {

	}

	/** {@code status} defaults to PENDING; CANCELLED is only reachable through DELETE. */
	public record PaymentTransaction(@Size(max = 120) String id,
			                         @Size(max = 120) String transactionId,
			                         @Size(max = 120) String originalTransactionId,
			                         @Size(max = 255) String transactionHash,
			                         @NotBlank @Size(max = 64) String transactionType,
			                         @Size(max = 64) String transactionSubType,
			                         @Size(max = 64) String transferType,
			                         @Pattern(regexp = "^(PENDING|PROCESSING|COMPLETED|FAILED|REVERSED)$", message = "must be PENDING, PROCESSING, COMPLETED, FAILED or REVERSED") String status,
			                         boolean isSetteled,
			                         boolean isLocked
			                         ) {

	}

	public record PaymentAdditionalInformation(@Size(max = 500) String memo,
			                                   @Size(max = 32) String network,
			                                   @Size(max = 64) String provider,
			                                   Boolean includeNetworkFee,
			                                   @Size(max = 32) String feeCollectedFrom,
			                       			   @Size(max = 32) String feeRuleType,
			                    			   @DecimalMin("0") @DecimalMax("100") BigDecimal feeRulePercent,
			                    			   @PositiveOrZero BigDecimal freeRuleAmount,
			                    			   @Size(max = 1000) String description,
			                    			   @Size(max = 140) String reference,
			                    			   @Size(max = 2000) String fundingInstructions
			                                   ) {

	}

	/**
	 * Filters for the paginated payment search. Every field is optional and they
	 * combine with AND. beneficiaryName is a partial, case-insensitive match;
	 * amounts and createdAt are inclusive ranges; everything else is exact.
	 * CANCELLED payments are only returned when status = CANCELLED is asked for.
	 */
	public record PaymentSearchRequest(
			String transactionId,
			String originalTransactionId,
			String transactionHash,
			String transactionType,
			String transferType,
			PaymentStatus status,
			Boolean isSettled,
			Boolean isLocked,
			String userId,
			String sourceWalletId,
			String destinationWalletId,
			String sourceCurrency,
			String destinationCurrency,
			String beneficiaryId,
			@Size(max = 140) String beneficiaryName,
			String quoteId,
			@PositiveOrZero BigDecimal minTotalAmount,
			@PositiveOrZero BigDecimal maxTotalAmount,
			Instant createdFrom,
			Instant createdTo) {
	}

	public record PaymentResponse(

			UUID id,
			Quote quote, 
			Payer payer, 
			Payee payee,
			TransactionAmount transactionAmount,
			PaymentTransaction paymentTransaction, 
			PaymentAdditionalInformation paymentAdditionalInformation) {}

}
