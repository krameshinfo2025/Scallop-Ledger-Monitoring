/**
 * 
 */
package com.scallop.ledger.constant;

/**
 * 
 */
public enum AcountHolderAccountType {
	
	SAVING("Savings Account"), 
	CHECKING("Checking Account / Current Account"), 
	SALARY("Salary Account"), 
	FIXEDDEPOSIT("Fixed Deposit (FD) / Certificate of Deposit (CD)"), 
	RECURINGDEPOSIT("Recurring Deposit (RD)"), 
	HIGHYIELD("High-Yield Savings / Money Market Account"), 
	MULTICURRENCY("Multi-Currency Account (MCA)"), 
	OFFSHORE("Non-Resident / Offshore Account"), 
	BUSINESSCURRENT("Business / Commercial Current Account");
	
	private final String description;

	/**
	 * @param description
	 */
	private AcountHolderAccountType(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	

}
