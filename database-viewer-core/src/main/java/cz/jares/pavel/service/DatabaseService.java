package cz.jares.pavel.service;

import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;

import cz.jares.pavel.dto.PageableDto;
import cz.jares.pavel.dto.ResultListDto;
import cz.jares.pavel.dto.TableStatistics;

/**
 * 
 * This service offer base database operation on specific connection
 * 
 * @author jaresp
 *
 */
@Transactional
public interface DatabaseService {

	/**
	 * Get existing schemas in database identified by connectionId
	 * 
	 * @param connectionId - id of connection stored in database
	 * @return list of schema's name
	 * @throws SQLException in case of database problems
	 */
	@Cacheable("schema")
	@Transactional(TxType.REQUIRED)
	public List<String> getSchemas(Long connectionId) throws SQLException;
	
	/**
	 * Get existing tables in database identified by connectionId
	 * 
	 * @param connectionId - id of connection stored in database
	 * @return list of tables names
	 * @throws SQLException in case of database problems
	 */
	@Cacheable("tables")
	@Transactional(TxType.REQUIRED)
	public List<String> getTables(Long connectionId) throws SQLException;
	
	/**
	 *  Get list of columns name in database identified by connectionId table in there,
	 * identified by tableName  
	 * 
	 * @param connectionId - id of connection stored in database
	 * @param tableName - name of table to fetch columns
	 * @return - list of columns names
	 * @throws SQLException in case of database problems
	 */
	@Cacheable("columns")
	@Transactional(TxType.REQUIRED)
	public List<String> getColumns(Long connectionId, String tableName) throws SQLException;
	
	/**
	 * 	Method fetch page from database identified by connectionId and table with name tableName.
	 * 
	 * @param connectionId - id of connection stored in database
	 * @param tableName - name of table to fetch data
	 * @param pageable - pageable object
	 * @return result list with data of specific page
	 * @throws DataAccessException in case of database settings problems (ie. wrong access rights)
	 * @throws SQLException in case of database problems
	 */
	@Cacheable(value = "data", key = "{#connectionId, #tableName, #pageable}")
	@Transactional(TxType.REQUIRED)
	public ResultListDto getList(Long connectionId, String tableName, PageableDto pageable) throws DataAccessException, SQLException;

	/**
	 *  Method fetch base statistics (min, avg, mediam and max value for each column in table)
	 * 
	 * @param connectionId - id of connection stored in database
	 * @param tableName - table name to fetch statistics
	 * @return table statistics (min, max, avg, median value for each column)
	 * @throws SQLException in case of database problems
	 * @throws DataAccessException in case of database settings problems (ie. wrong access rights)
	 */
	@Cacheable(value = "statistics", key = "{#connectionId, #tableName}")
	@Transactional(TxType.REQUIRED)
	public TableStatistics getStatistics(Long connectionId, String tableName) throws DataAccessException, SQLException;
	
}
