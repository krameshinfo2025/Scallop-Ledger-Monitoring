/**
 ***********************************************************************************
 LedgerEntitiyServiceImpl.Java
 version 1.0
 28,September,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.LedgerEntity;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.repo.LedgerEntityRepository;
import com.scallop.ledger.request.RequestData.CreateLedgerEntitiyRequest;
import com.scallop.ledger.response.PagedResponse;
import com.scallop.ledger.response.ResponseData.LedgerEntiryResponse;
import com.scallop.ledger.service.AuditService;
import com.scallop.ledger.service.LedgerEntityService;

/**
 * LedgerEntitiyServiceImpl is a class act as business logic implementation
 * for Ledger Entity Operations
 * 
 */
@Service(value = "ledgerEntitiyService")
public class LedgerEntityServiceImpl implements LedgerEntityService, Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Place Holder for auditService of type AuditService */
	private final AuditService auditService;
	
	/** The Place Holder for ledgerEntityRepository of type LedgerEntityRepository */
	private final LedgerEntityRepository ledgerEntityRepository;

	/**
	 * @param auditService
	 * @param ledgerEntityRepository
	 */
	public LedgerEntityServiceImpl(AuditService auditService, LedgerEntityRepository ledgerEntityRepository) {
		super();
		this.auditService = auditService;
		this.ledgerEntityRepository = ledgerEntityRepository;
	}


	/**
	 * Method for create New Ledger Entity in the System.
	 * 
	 * @param createLedgerEntitiyRequest - user input for create Ledger Entity of type RequestData.CreateLedgerEntitiyRequest.
	 * @param actor - the authentication of type Authentication passed in the request header.
	 *
 	 * @return data containing the UserResponse of type ResponseData.UserResponse.
	 * @throws LedgerException if any 
	 */
	public LedgerEntiryResponse create(CreateLedgerEntitiyRequest createLedgerEntitiyRequest, String actor) {
		
		Optional<LedgerEntity> existingLedgerEntity =  ledgerEntityRepository.findById(createLedgerEntitiyRequest.entityId());

		if (existingLedgerEntity.isPresent()) {
			throw new LedgerException("ENTITY_ID_TAKEN", "That Entity is already in use.");
		}
		
		LedgerEntity ledgerEntity = new LedgerEntity(createLedgerEntitiyRequest.entityId(),createLedgerEntitiyRequest.entityName());
		
		ledgerEntity = ledgerEntityRepository.saveAndFlush(ledgerEntity);
		
		auditService.recordIndependently(actor, "LEDGER_ENTITY_CREATED", ledgerEntity.getEntityId(), ledgerEntity.getEntityId(),
				ledgerEntity.getEntityName() + " as " + ledgerEntity.getEntityId());

		
		LedgerEntiryResponse ledgerEntiryResponse = new LedgerEntiryResponse(ledgerEntity.getEntityId(),ledgerEntity.getEntityName());
		
		return ledgerEntiryResponse;
	};

	
  	/**
  	 * Method for Fetch All App System User in the System.
  	 * 
  	 * @param pageable - user input for Fetch all Ledger Entities of type Pageable.
  	 * @param actor - the authentication of type Authentication passed in the request header.
  	 *
   	 * @return data containing the LedgerEntiryResponse List of type PagedResponse.LedgerEntiryResponse.
  	 * @throws LedgerException if any 
  	 */
	@Transactional(readOnly = true)
	public PagedResponse<LedgerEntiryResponse> getAllLedgerEntities(Pageable pageable, String actor){
		
		Page<LedgerEntity> dataList = ledgerEntityRepository.findAll(pageable);
		
		List<LedgerEntity> ledgerEntityList  = dataList.getContent();
		
		List<LedgerEntiryResponse> ledgerResponseList = ledgerEntityList.stream()
	            .map(source -> new LedgerEntiryResponse(
	            		source.getEntityId(),
	            		source.getEntityName()))
	            .toList(); 
		

		PagedResponse<LedgerEntiryResponse> response = new PagedResponse<LedgerEntiryResponse>(ledgerResponseList,
				pageable.getPageNumber() + 1, dataList.getContent().size(), dataList.getTotalElements(),
				dataList.getTotalPages(), dataList.hasNext());
		
		return response;
	};


}
