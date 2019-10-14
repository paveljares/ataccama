package cz.jares.pavel.service;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.service.impl.ConnectionDataPoolImpl;

/**
 * 
 *  This service is responsible for managing connection to databases.
 *  As a result is posibble to get DataSource or directly JdbcTemplate.
 *  
 *  {@link ConnectionDataPoolImpl} is implementation, which support pooled
 * data sources.
 * 
 * @author jaresp
 *
 */
public interface ConnectionDataPool {

	public DataSource createDataSource(ConnectionDto connectionDto);

	public JdbcTemplate getJdbcTemplate(Long id);

	public void release(Long connectionId);
	
}
