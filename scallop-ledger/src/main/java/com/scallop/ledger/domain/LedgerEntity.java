/**
 ***********************************************************************************
 LedgerEntitiy.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * 
 */
@Entity
@Table(name = "ledger_entities")
public class LedgerEntity {
	
	/** Primary key.*/
	@Id
	@NotBlank
	@Column(name = "entity_id", nullable = false, columnDefinition = "text")
	private String entityId;
	
	@NotBlank
	@Column(name = "name", nullable = false, columnDefinition = "text")
	private String entityName;

	/**
	 * 
	 */
	public LedgerEntity() {
		// for JPA
	}

	/**
	 * @param entityId
	 * @param entityName
	 */
	public LedgerEntity(@NotBlank String entityId, @NotBlank String entityName) {
		super();
		this.entityId = entityId;
		this.entityName = entityName;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}



}
