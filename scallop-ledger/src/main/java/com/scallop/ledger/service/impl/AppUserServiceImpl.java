/**
 ***********************************************************************************
 AppUserServiceImpl.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.constant.Role;
import com.scallop.ledger.domain.AppUser;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.AppUserRepository;
import com.scallop.ledger.request.RequestData.CreateUserRequest;
import com.scallop.ledger.request.RequestData.UpdateUserRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.UserResponse;
import com.scallop.ledger.service.AppUserService;
import com.scallop.ledger.service.AuditService;

/**
 * AppUserServiceImpl is a class act as business logic implementation
 * for App System User Operations
 * 
 */
@Service(value = "appUserService")
public class AppUserServiceImpl implements AppUserService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String ENTITY = "app_user";

	/** The Place Holder for appUserRepository of type AppUserRepository */
	private final AppUserRepository appUserRepository;

	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;

	/** The Place Holder for encoder of type PasswordEncoder */
	private final PasswordEncoder encoder;

	/**
	 * @param appUserRepository
	 * @param auditService
	 * @param encoder
	 */
	public AppUserServiceImpl(AppUserRepository appUserRepository, AuditService auditService, PasswordEncoder encoder) {
		super();
		this.appUserRepository = appUserRepository;
		this.auditService = auditService;
		this.encoder = encoder;
	}

	/**
	 * Deliberately not read-only: login checks the password and enabled flag here,
	 * so it must read the writer and never a lagging replica.
	 */
	@Override
	public Optional<AppUser> findByUsername(String username) {

		return appUserRepository.findByUsername(username);
	}

	/**
	 * Method for create New App System User in the System.
	 * 
	 * @param createUserRequest - user input for create New App System User of type
	 *                          RequestData.CreateUserRequest.
	 * @param authentication    - the authentication of type Authentication passed
	 *                          in the request header.
	 *
	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any
	 */
	@Transactional
	public UserResponse create(CreateUserRequest createUserRequest, String actor) {

		Optional<AppUser> existingUser = findByUsername(createUserRequest.username());

		if (existingUser.isPresent()) {
			throw new LedgerException("USERNAME_TAKEN", "That username is already in use.");
		}

		AppUser user = new AppUser(createUserRequest.username(), encoder.encode(createUserRequest.password()),
				createUserRequest.role(),true,Instant.now(),Instant.now(),actor,actor);

		user = appUserRepository.save(user);

		auditService.recordIndependently(actor, "USER_CREATED", ENTITY, user.getId(),
				user.getUsername() + " as " + user.getRole());

		return UserResponse.of(user);

	}

	/**
	 * Method for Fetch All App System User in the System.
	 * 
	 * @param pageable       - user input for Fetch all App System User of type
	 *                       Pageable.
	 * @param authentication - the authentication of type Authentication passed in
	 *                       the request header.
	 *
	 * @return data containing the UserResponse List of type
	 *         PagedResponse.UserResponse.
	 * @throws LedgerException if any
	 */
	@Transactional(readOnly = true)
	public PagedResponse<UserResponse> getAllAppUsers(Pageable pageable, String actor) {

		Page<AppUser> userList = appUserRepository.findAll(pageable);

		List<AppUser> appUsersList = userList.getContent();

	
		List<UserResponse> userResponseList = appUsersList.stream()
	            .map(source -> new UserResponse(
	            		source.getId(), source.getUsername(), source.getRole(),
	            		source.isEnabled()))
	            .toList(); 
		

		PagedResponse<UserResponse> response = new PagedResponse<UserResponse>(userResponseList,
				pageable.getPageNumber() + 1, userList.getContent().size(), userList.getTotalElements(),
				userList.getTotalPages(), userList.hasNext());

		return response;

	}

	/**
	 * Method for Enable/disable a user or change their role of the App System User
	 * in the System.
	 * 
	 * @param id                 - a path variable and user identifier of type UUID.
	 * @param updateUserRequest- user input for Update App System User of type
	 *                           RequestData.UpdateUserRequest.
	 * @param authentication     - the authentication of type Authentication passed
	 *                           in the request header.
	 *
	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any
	 */
	@Transactional
	public UserResponse updateStatusOrRole(UUID id, UpdateUserRequest updateUserRequest, String actor) {

		AppUser user = null;
		Optional<AppUser> existingUser = appUserRepository.findById(id);

		if (existingUser.isPresent()) {

			Optional<AppUser> currentUser = appUserRepository.findById(UUID.fromString(actor));

			// An admin locking themselves out is the easiest way to end up with no
			// admin at all, so self-disable and self-demotion are refused outright.
			if (existingUser.get().getId().equals(currentUser.get().getId())) {

				boolean disabling = Boolean.FALSE.equals(updateUserRequest.enabled());

				boolean demoting = updateUserRequest.role() != null && updateUserRequest.role() != Role.ADMIN;

				if (disabling || demoting) {

					throw new LedgerException(HttpStatus.BAD_REQUEST.value(), "SELF_LOCKOUT",
							"You cannot disable or demote your own account.");
				}
			}

			boolean enable = existingUser.get().isEnabled();
			
			if(updateUserRequest.enabled() != null && !(updateUserRequest.role()!= null)) {
				
				if (enable == updateUserRequest.enabled()) {

					if (Boolean.FALSE.equals(enable)) {
						throw new LedgerException(HttpStatus.CONFLICT.value(), "CONFLICT", "User already inactivated.");
					} else {
						throw new LedgerException(HttpStatus.CONFLICT.value(), "CONFLICT", "User already activated.");
					}

				}

			}


			user = existingUser.get();

			StringBuilder changes = new StringBuilder();
			if (updateUserRequest.enabled() != null && updateUserRequest.enabled() != enable) {
				user.setEnabled(updateUserRequest.enabled());
				changes.append("enabled=").append(updateUserRequest.enabled()).append(' ');
			}
			if (updateUserRequest.role() != null && updateUserRequest.role() != user.getRole()) {
				changes.append("role=").append(user.getRole()).append("->").append(updateUserRequest.role());
				user.setRole(updateUserRequest.role());
			}
			
			user.setUpdatedAt(Instant.now());
			user.setUpdatedBy(actor);

			user = appUserRepository.save(user);
			
			if (!changes.isEmpty()) {
				auditService.recordIndependently(actor, "USER_UPDATED", ENTITY, user.getId(),
						changes.toString().trim());
			}


		} else {

			throw new LedgerException(HttpStatus.NO_CONTENT.value(), "NOT_FOUND", "User is not found in the system.");
		}

		return UserResponse.of(user);

	}

	/**
	 * Method for Enable/disable a user or change their role of the App System User
	 * in the System.
	 * 
	 * @param id                 - a path variable and user identifier of type UUID.
	 * @param updateUserRequest- user input for Update App System User of type
	 *                           RequestData.ResetPasswordRequest.
	 * @param actor              - the authentication of type Authentication passed
	 *                           in the request header.
	 *
	 * @return
	 * @throws LedgerException if any
	 */
	@Transactional
	public void resetPassword(UUID id, String newPassword, String actor) {

		Optional<AppUser> existingUser = appUserRepository.findById(id);

		if (existingUser.isPresent()) {

			AppUser user = existingUser.get();

			user.setPasswordHash(encoder.encode(newPassword));
			user.setUpdatedAt(Instant.now());
			user.setUpdatedBy(actor);

			user = appUserRepository.saveAndFlush(user);

			auditService.recordIndependently(actor, "PASSWORD_RESET", ENTITY, user.getId(), null);

		} else {

			throw new LedgerException(HttpStatus.NO_CONTENT.value(), "NOT_FOUND", "User is not found in the system.");

		}

	}

}
