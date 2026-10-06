/**
 ***********************************************************************************
 PaymentMapper.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.helper;

import com.scallop.ledger.constant.PaymentStatus;
import com.scallop.ledger.domain.FxQuote;
import com.scallop.ledger.domain.Payment;
import com.scallop.ledger.domain.PaymentAdditionalInfo;
import com.scallop.ledger.domain.PaymentAmount;
import com.scallop.ledger.domain.PaymentPayee;
import com.scallop.ledger.domain.PaymentPayer;
import com.scallop.ledger.request.PaymentRequestData.Payee;
import com.scallop.ledger.request.PaymentRequestData.Payer;
import com.scallop.ledger.request.PaymentRequestData.PaymentAdditionalInformation;
import com.scallop.ledger.request.PaymentRequestData.PaymentRequest;
import com.scallop.ledger.request.PaymentRequestData.PaymentResponse;
import com.scallop.ledger.request.PaymentRequestData.PaymentTransaction;
import com.scallop.ledger.request.RequestData.Quote;
import com.scallop.ledger.request.PaymentRequestData.TransactionAmount;

/**
 * Copies between the API records and the payment entities. Updates are applied
 * in place, so a PUT keeps the existing child rows (and their ids) instead of
 * orphaning them and inserting new ones. The quote is resolved by the service
 * and passed in, never built from the request.
 */
public final class PaymentMapper {

	private PaymentMapper() {
	}

	/** Overwrites every request-owned field of {@code target} - PUT semantics. */
	public static void apply(PaymentRequest request, FxQuote fxQuote, Payment target) {

		target.setFxQuote(fxQuote);

		PaymentTransaction transaction = request.paymentTransaction();
		target.setExternalId(trimToNull(transaction.id()));
		target.setTransactionId(trimToNull(transaction.transactionId()));
		target.setOriginalTransactionId(trimToNull(transaction.originalTransactionId()));
		target.setTransactionHash(trimToNull(transaction.transactionHash()));
		target.setTransactionType(transaction.transactionType().trim());
		target.setTransactionSubType(trimToNull(transaction.transactionSubType()));
		target.setTransferType(trimToNull(transaction.transferType()));
		target.setStatus(transaction.status() == null ? PaymentStatus.PENDING : PaymentStatus.valueOf(transaction.status()));
		target.setSettled(transaction.isSetteled());
		target.setLocked(transaction.isLocked());

		PaymentPayer payer = target.getPayer() != null ? target.getPayer() : new PaymentPayer();
		applyPayer(request.payer(), payer);
		target.setPayer(payer);

		PaymentPayee payee = target.getPayee() != null ? target.getPayee() : new PaymentPayee();
		applyPayee(request.payee(), payee);
		target.setPayee(payee);

		PaymentAmount amount = target.getAmount() != null ? target.getAmount() : new PaymentAmount();
		applyAmount(request.transactionAmount(), amount);
		target.setAmount(amount);

		target.setAdditionalInfo(applyAdditionalInfo(request.paymentAdditionalInformation(), target.getAdditionalInfo()));
	}

	private static void applyPayer(Payer source, PaymentPayer payer) {
		payer.setUserId(source.userId().trim());
		payer.setSourceWalletId(trimToNull(source.sourceWalletId()));
		payer.setSourceCurrency(source.sourceCurrency());
		payer.setSourceAmount(source.sourceAmount());
		payer.setSourceAddress(source.sourceAddress());
		payer.setSourceImage(source.sourceImage());
	}

	private static void applyPayee(Payee source, PaymentPayee payee) {
		payee.setDestinationWalletId(trimToNull(source.destinationWalletId()));
		payee.setDestinationCurrency(source.destinationCurrency());
		payee.setDestinationAmount(source.destinationAmount());
		payee.setDestinationAddress(source.destinationAddress());
		payee.setDestinationImage(source.destinationImage());
		payee.setBeneficiaryInSystem(Boolean.TRUE.equals(source.isBeneficiaryInSytem()));
		payee.setBeneficiaryId(trimToNull(source.beneficiaryId()));
		payee.setBeneficiaryName(trimToNull(source.beneficiaryName()));
		payee.setAccountNumberOrIban(trimToNull(source.accountNumberOrIBAN()));
		payee.setCodeSwiftOrBic(source.codeSwiftorBIC());
		payee.setCodeRoutingOrLocal(trimToNull(source.codeRoutingOrLocal()));
		payee.setBankAddress(source.bankAddress());
	}

	private static void applyAmount(TransactionAmount source, PaymentAmount amount) {
		amount.setPrincipleAmount(source.principleAmount());
		amount.setFeeAmount(source.feeAmount());
		amount.setNetworkFee(source.networkFee());
		amount.setOtherFee(source.otherFee());
		amount.setTotalFee(source.totalFee());
		amount.setFeeInUsd(source.feeInUSD());
		amount.setDiscount(source.discount());
		amount.setNetGrossAmount(source.netGrossAmount());
		amount.setTotalAmount(source.totalAmount());
	}

	/** Returns null when the request has none, so orphanRemoval drops the old row. */
	private static PaymentAdditionalInfo applyAdditionalInfo(PaymentAdditionalInformation source,
			PaymentAdditionalInfo existing) {
		if (source == null) {
			return null;
		}
		PaymentAdditionalInfo info = existing != null ? existing : new PaymentAdditionalInfo();
		info.setMemo(source.memo());
		info.setNetwork(source.network());
		info.setProvider(source.provider());
		info.setIncludeNetworkFee(source.includeNetworkFee());
		info.setFeeCollectedFrom(source.feeCollectedFrom());
		info.setFeeRuleType(source.feeRuleType());
		info.setFeeRulePercent(source.feeRulePercent());
		info.setFreeRuleAmount(source.freeRuleAmount());
		info.setDescription(source.description());
		info.setReference(source.reference());
		info.setFundingInstructions(source.fundingInstructions());
		return info;
	}

	public static PaymentResponse toResponse(Payment payment) {
		return new PaymentResponse(
				payment.getId(),
				toQuote(payment.getFxQuote()),
				toPayer(payment.getPayer()),
				toPayee(payment.getPayee()),
				toAmount(payment.getAmount()),
				toTransaction(payment),
				toAdditionalInformation(payment.getAdditionalInfo()));
	}

	private static Quote toQuote(FxQuote quote) {
		if (quote == null) {
			return null;
		}
		return new Quote(quote.getQuoteId(), quote.getEntityId(), quote.getBaseCurrency(), quote.getTargetCurrency(),
				quote.getRate(), quote.getObservedAt(), quote.getExpiresAt(), quote.getSourceReference());
	}

	private static Payer toPayer(PaymentPayer payer) {
		return new Payer(payer.getUserId(), payer.getSourceWalletId(), payer.getSourceCurrency(),
				payer.getSourceAmount(), payer.getSourceAddress(), payer.getSourceImage());
	}

	private static Payee toPayee(PaymentPayee payee) {
		return new Payee(payee.getDestinationWalletId(), payee.getDestinationCurrency(), payee.getDestinationAmount(),
				payee.getDestinationAddress(), payee.getDestinationImage(), payee.isBeneficiaryInSystem(),
				payee.getBeneficiaryId(), payee.getBeneficiaryName(), payee.getAccountNumberOrIban(),
				payee.getCodeSwiftOrBic(), payee.getCodeRoutingOrLocal(), payee.getBankAddress());
	}

	private static TransactionAmount toAmount(PaymentAmount amount) {
		return new TransactionAmount(amount.getPrincipleAmount(), amount.getFeeAmount(), amount.getNetworkFee(),
				amount.getOtherFee(), amount.getTotalFee(), amount.getFeeInUsd(), amount.getDiscount(),
				amount.getNetGrossAmount(), amount.getTotalAmount());
	}

	private static PaymentTransaction toTransaction(Payment payment) {
		return new PaymentTransaction(payment.getExternalId(), payment.getTransactionId(),
				payment.getOriginalTransactionId(), payment.getTransactionHash(), payment.getTransactionType(),
				payment.getTransactionSubType(), payment.getTransferType(), payment.getStatus().name(),
				payment.isSettled(), payment.isLocked());
	}

	private static PaymentAdditionalInformation toAdditionalInformation(PaymentAdditionalInfo info) {
		if (info == null) {
			return null;
		}
		return new PaymentAdditionalInformation(info.getMemo(), info.getNetwork(), info.getProvider(),
				info.getIncludeNetworkFee(), info.getFeeCollectedFrom(), info.getFeeRuleType(),
				info.getFeeRulePercent(), info.getFreeRuleAmount(), info.getDescription(), info.getReference(),
				info.getFundingInstructions());
	}

	private static String trimToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
