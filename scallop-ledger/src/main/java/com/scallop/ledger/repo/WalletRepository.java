/**
 ***********************************************************************************
 WalletRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scallop.ledger.domain.Wallet;

/**
 * 
 */
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

	@Query(value ="SELECT * FROM wallet wallet "
			+ "WHERE (wallet.id = :id  OR :id IS NULL) "
			+ "OR (wallet.holder_id = :holderId  OR :holderId IS NULL) "
			+ "OR (wallet.currency_code = :currency  OR :currency IS NULL)",nativeQuery = true)
	Page<Wallet> findWallets(Pageable pageable,@Param("id") UUID id, @Param("holderId") UUID holderId, @Param("currency") String currency);


}
