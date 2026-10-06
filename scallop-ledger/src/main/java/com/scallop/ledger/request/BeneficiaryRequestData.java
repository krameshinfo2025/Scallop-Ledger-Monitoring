/**
 *
 */
package com.scallop.ledger.request;

import java.util.UUID;

import com.scallop.ledger.constant.BeneficiaryStatus;
import com.scallop.ledger.constant.BeneficiaryType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request and response shapes for Beneficiary Management. Shape-level checks
 * live here; rules that span fields (account number or IBAN, name by holder
 * type, known currency) are enforced in the service and by the database.
 */
public class BeneficiaryRequestData {

	public record BeneficiaryRequest(

			@NotBlank @Size(max = 32) BeneficiaryType beneficiaryType,
			Boolean isThirdParty,
			@NotBlank @Pattern(regexp = "^[A-Z]{3,10}$", message = "must be an upper-case currency code") String currencyCode,
			@NotNull @Valid BankDetails bank,
			@NotNull @Valid AccountHolderDetails accountHolder,
			UUID referenceId
			) {}

	public record  BankDetails(@Size(max = 140) String name,
			@Valid AccountHolderAddress address,
			@Size(max = 32) String type,
			@Size(max = 40) String accountNumber,
			@Size(max = 20) String routingNumber,
			@Size(max = 20) String transitNumber,
			@Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "must be a valid IFSC code") String ifscCode,
			@Size(max = 10) String sortCode,
			@Size(max = 10) String bsbNumber,
			@Size(max = 20) String nccNumber,
			@Pattern(regexp = "^\\d{18}$", message = "must be 18 digits") String clabeNumber,
			@Size(max = 20) String bankCode,
			@Size(max = 20) String branchCode,
			@Size(max = 20) String cnapsCode,
			@Pattern(regexp = "^\\d{10}$", message = "must be 10 digits") String nubanCode,
			@Size(max = 20) String clearingCode,
			@Size(max = 77) String pixCode,
			@Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$", message = "must be a valid IBAN without spaces") String iban,
			@Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "must be an 8 or 11 character BIC") String bicSwift) {}

	public record AccountHolderAddress(@Size(max = 140) String streetLine1,
			@Size(max = 140) String streetLine2,
			@Size(max = 80) String city,
			@Size(max = 80) String stateOrProvince,
			@Size(max = 80) String stateRegionOrProvince,
			@Size(max = 20) String postalCode,
			@Pattern(regexp = "^[A-Z]{2}$", message = "must be an ISO 3166 alpha-2 code") String countryCode) {}

	public record AccountHolderDetails(
			@NotBlank @Pattern(regexp = "^(INDIVIDUAL|BUSINESS)$", message = "must be INDIVIDUAL or BUSINESS") String type,
			@Size(max = 80) String firstName,
			@Size(max = 80) String lastName,
			@Pattern(regexp = "^\\+?[0-9 ()-]{6,20}$", message = "must be a phone number") String phone,
			@Email @Size(max = 254) String email,
			@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "must be an ISO date (yyyy-MM-dd)") String dateOfBirth,
			@Valid AccountHolderAddress address,
			@Pattern(regexp = "^[A-Z]{2}$", message = "must be an ISO 3166 alpha-2 code") String citizenship,
			@Size(max = 140) String businessName,
			@Size(max = 40) String taxIdentificationNumber){}

	/**
	 * Filters for the paginated beneficiary search. Every field is optional and
	 * they combine with AND. Name matching is partial and case-insensitive;
	 * everything else is an exact match. DELETED beneficiaries are only returned
	 * when status = DELETED is asked for explicitly.
	 */
	public record BeneficiarySearchRequest(
			String beneficiaryType,
			Boolean isThirdParty,
			String currencyCode,
			UUID referenceId,
			BeneficiaryStatus status,
			@Size(max = 140) String holderName,
			String holderType,
			String holderEmail,
			String holderPhone,
			String holderCountryCode,
			String accountNumber,
			String iban,
			String bicSwift,
			@Size(max = 140) String bankName,
			String bankCountryCode) {}


    public record BeneficiaryResponse(

    		UUID beneficiaryId,
			String beneficiaryType,
			Boolean isThirdParty,
			String currencyCode,
			BankDetails bank,
			AccountHolderDetails accountHolder,
			UUID referenceId
			) {}

}
