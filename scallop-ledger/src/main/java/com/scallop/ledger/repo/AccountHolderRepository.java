/**
 ***********************************************************************************
 AccountHolderRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scallop.ledger.domain.AccountHolder;

/**
 * 
 */
public interface AccountHolderRepository  extends JpaRepository<AccountHolder, UUID> {

	@Query(value ="SELECT * FROM account_holder accountholder "
			+ "WHERE (accountholder.status = :status  OR :status IS NULL) "
			+ "OR (accountholder.holder_type = :holderType  OR :holderType IS NULL) "
			+ "OR (accountholder.account_number = :accountNumber OR :accountNumber IS NULL) "
			+ "OR (accountholder.customer_id = :customerId  OR :customerId IS NULL) "
			+ "OR (accountholder.entity_id = :entityId  OR :entityId IS NULL)",nativeQuery = true)
	Page<AccountHolder> findAccountHolder(Pageable pageable,
			@Param("entityId") String entityId, 
			@Param("customerId") String customerId, 
			@Param("accountNumber") String accountNumber,
			@Param("holderType")String holderType, 
			@Param("status") String status);
	
	
	@Query(value ="SELECT * FROM account_holder accountholder "
			+ "WHERE (accountholder.holder_type = :holderType  OR :holderType IS NULL) "
			+ "OR (accountholder.account_number = :accountNumber OR :accountNumber IS NULL) "
			+ "OR (accountholder.customer_id = :customerId  OR :customerId IS NULL) "
			+ "OR (accountholder.entity_id = :entityId  OR :entityId IS NULL)",nativeQuery = true)
	Optional<AccountHolder> findAccountHolder(@Param("entityId") String entityId, 
			@Param("customerId") String customerId, 
			@Param("accountNumber") String accountNumber,
			@Param("holderType")String holderType);

	

}
