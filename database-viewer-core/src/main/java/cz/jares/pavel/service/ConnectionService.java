package cz.jares.pavel.service;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.persistence.entity.Connection;
import cz.jares.pavel.persistence.repository.ConnectionRepository;

/**
 * 
 *  This service is responsible for base database operation with entity {@link Connection},
 * it wrap repository {@link ConnectionRepository} (transformation between {@link Connection} and
 * {@link ConnectionDto}).
 * 
 * @author jaresp
 *
 */
@Transactional
public interface ConnectionService {

	/**
	 * Find and convert connection by id
	 * If it is missing, return null
	 * 
	 * @param id - id of connection stored in database
	 * @return connection DTO
	 */
	@Transactional(TxType.REQUIRED)
	public ConnectionDto findOne(Long id);
	
	/**
	 * Fetch all stored connections
	 * If it is missing, return empty list
	 * 
	 * @return list of connection DTO
	 */
	@Transactional(TxType.REQUIRED)
	public List<ConnectionDto> getAll();
	
	/**
	 * Create a new connection record in database
	 * 
	 * @param connectionDto DTO with parameters for new connection
	 * @return DTO of created connection record
	 */
	@Transactional(TxType.REQUIRED)
	public ConnectionDto create(ConnectionDto connectionDto);

	/**
	 * Update an existing record in database
	 * 
	 * @param connectionDto DTO with new parameters
	 * @return DTO of created connection record
	 */
	@Transactional(TxType.REQUIRED)
	public ConnectionDto update(ConnectionDto connectionDto);

	/**
	 * Delete one specific record in database by id.
	 * If it is missing, do nothing
	 * 
	 * @param id - id of connection stored in database
	 */
	@Transactional(TxType.REQUIRED)
	public void delete(Long id);
	
}
