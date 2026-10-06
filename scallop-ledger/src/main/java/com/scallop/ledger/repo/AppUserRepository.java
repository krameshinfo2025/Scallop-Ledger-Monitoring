/**
 ***********************************************************************************
 AppUserRepository.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scallop.ledger.constant.Role;
import com.scallop.ledger.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByRole(Role role);
    
    /**
     * Usernames whose stored hash is not a well-formed BCrypt string (same shape
     * BCryptPasswordEncoder accepts). Such users can never log in.
     */
    @Query(value = "select username from app_user where password_hash !~ '^\\$2[aby]?\\$[0-9]{2}\\$[./0-9A-Za-z]{53}$'",
            nativeQuery = true)
    List<String> findUsernamesWithMalformedPasswordHash();

}
