/**
 ***********************************************************************************
 BeneficiaryMapper.Java
 version 1.0
 04,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.helper;

import java.time.LocalDate;
import java.util.Locale;

import com.scallop.ledger.domain.Beneficiary;
import com.scallop.ledger.domain.BeneficiaryAccountHolder;
import com.scallop.ledger.domain.BeneficiaryAddress;
import com.scallop.ledger.domain.BeneficiaryBank;
import com.scallop.ledger.request.BeneficiaryRequestData.AccountHolderAddress;
import com.scallop.ledger.request.BeneficiaryRequestData.AccountHolderDetails;
import com.scallop.ledger.request.BeneficiaryRequestData.BankDetails;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryRequest;
import com.scallop.ledger.request.BeneficiaryRequestData.BeneficiaryResponse;

/**
 * Copies between the API records and the beneficiary entities. Updates are
 * applied in place, so a PUT keeps the existing child rows (and their ids)
 * instead of orphaning them and inserting new ones.
 */
public final class BeneficiaryMapper {

	private BeneficiaryMapper() {
	}

	/** Overwrites every request-owned field of {@code target} - PUT semantics. */
	public static void apply(BeneficiaryRequest request, Beneficiary target) {

		target.setBeneficiaryType(request.beneficiaryType().toString().trim());
		target.setThirdParty(Boolean.TRUE.equals(request.isThirdParty()));
		target.setCurrencyCode(request.currencyCode());
		target.setReferenceId(request.referenceId());

		BeneficiaryBank bank = target.getBank() != null ? target.getBank() : new BeneficiaryBank();
		applyBank(request.bank(), bank);
		target.setBank(bank);

		BeneficiaryAccountHolder holder = target.getAccountHolder() != null ? target.getAccountHolder()
				: new BeneficiaryAccountHolder();
		applyHolder(request.accountHolder(), holder);
		target.setAccountHolder(holder);
	}

	private static void applyBank(BankDetails source, BeneficiaryBank bank) {
		bank.setName(source.name());
		bank.setAddress(applyAddress(source.address(), bank.getAddress()));
		bank.setType(source.type());
		bank.setAccountNumber(source.accountNumber());
		bank.setRoutingNumber(source.routingNumber());
		bank.setTransitNumber(source.transitNumber());
		bank.setIfscCode(source.ifscCode());
		bank.setSortCode(source.sortCode());
		bank.setBsbNumber(source.bsbNumber());
		bank.setNccNumber(source.nccNumber());
		bank.setClabeNumber(source.clabeNumber());
		bank.setBankCode(source.bankCode());
		bank.setBranchCode(source.branchCode());
		bank.setCnapsCode(source.cnapsCode());
		bank.setNubanCode(source.nubanCode());
		bank.setClearingCode(source.clearingCode());
		bank.setPixCode(source.pixCode());
		bank.setIban(source.iban());
		bank.setBicSwift(source.bicSwift());
	}

	private static void applyHolder(AccountHolderDetails source, BeneficiaryAccountHolder holder) {
		holder.setType(source.type());
		holder.setFirstName(source.firstName());
		holder.setLastName(source.lastName());
		holder.setPhone(source.phone());
		holder.setEmail(source.email() == null ? null : source.email().trim().toLowerCase(Locale.ROOT));
		holder.setDateOfBirth(source.dateOfBirth() == null ? null : LocalDate.parse(source.dateOfBirth()));
		holder.setAddress(applyAddress(source.address(), holder.getAddress()));
		holder.setCitizenship(source.citizenship());
		holder.setBusinessName(source.businessName());
		holder.setTaxIdentificationNumber(source.taxIdentificationNumber());
	}

	/** Returns null when the request has no address, so orphanRemoval drops the old row. */
	private static BeneficiaryAddress applyAddress(AccountHolderAddress source, BeneficiaryAddress existing) {
		if (source == null) {
			return null;
		}
		BeneficiaryAddress address = existing != null ? existing : new BeneficiaryAddress();
		address.setStreetLine1(source.streetLine1());
		address.setStreetLine2(source.streetLine2());
		address.setCity(source.city());
		address.setStateOrProvince(source.stateOrProvince());
		address.setStateRegionOrProvince(source.stateRegionOrProvince());
		address.setPostalCode(source.postalCode());
		address.setCountryCode(source.countryCode());
		return address;
	}

	public static BeneficiaryResponse toResponse(Beneficiary beneficiary) {
		return new BeneficiaryResponse(
				beneficiary.getId(),
				beneficiary.getBeneficiaryType(),
				beneficiary.isThirdParty(),
				beneficiary.getCurrencyCode(),
				toBankDetails(beneficiary.getBank()),
				toHolderDetails(beneficiary.getAccountHolder()),
				beneficiary.getReferenceId());
	}

	private static BankDetails toBankDetails(BeneficiaryBank bank) {
		return new BankDetails(bank.getName(), toAddress(bank.getAddress()), bank.getType(),
				bank.getAccountNumber(), bank.getRoutingNumber(), bank.getTransitNumber(), bank.getIfscCode(),
				bank.getSortCode(), bank.getBsbNumber(), bank.getNccNumber(), bank.getClabeNumber(),
				bank.getBankCode(), bank.getBranchCode(), bank.getCnapsCode(), bank.getNubanCode(),
				bank.getClearingCode(), bank.getPixCode(), bank.getIban(), bank.getBicSwift());
	}

	private static AccountHolderDetails toHolderDetails(BeneficiaryAccountHolder holder) {
		return new AccountHolderDetails(holder.getType(), holder.getFirstName(), holder.getLastName(),
				holder.getPhone(), holder.getEmail(),
				holder.getDateOfBirth() == null ? null : holder.getDateOfBirth().toString(),
				toAddress(holder.getAddress()), holder.getCitizenship(), holder.getBusinessName(),
				holder.getTaxIdentificationNumber());
	}

	private static AccountHolderAddress toAddress(BeneficiaryAddress address) {
		if (address == null) {
			return null;
		}
		return new AccountHolderAddress(address.getStreetLine1(), address.getStreetLine2(), address.getCity(),
				address.getStateOrProvince(), address.getStateRegionOrProvince(), address.getPostalCode(),
				address.getCountryCode());
	}
}
