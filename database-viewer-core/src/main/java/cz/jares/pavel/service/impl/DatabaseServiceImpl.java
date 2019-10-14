package cz.jares.pavel.service.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import cz.jares.pavel.dto.ColumnStatistic;
import cz.jares.pavel.dto.PageableDto;
import cz.jares.pavel.dto.ResultListDto;
import cz.jares.pavel.dto.TableStatistics;
import cz.jares.pavel.service.DatabaseService;

/**
 * 
 * @author jaresp
 *
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {

	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Get bean of JdbcTemplate for connection with id = connectionId
	 * 
	 * @param connectionId
	 * @return
	 */
	private JdbcTemplate getJdbcTemplate(Long connectionId) {
		return (JdbcTemplate) applicationContext.getBean("customJdbcTemplate", connectionId);
	}
	
	/**
	 * Get metadata object from JdbcTemplate
	 * 
	 * @param jdbcTemplate
	 * @return
	 * @throws SQLException
	 */
	private DatabaseMetaData getDatabaseMetaData(JdbcTemplate jdbcTemplate) throws SQLException {
		return jdbcTemplate.getDataSource().getConnection().getMetaData();
	}
	
	/**
	 * Get metadata object by connectionId
	 * 
	 * @param connectionId
	 * @return
	 * @throws SQLException
	 */
	private DatabaseMetaData getDatabaseMetaData(Long connectionId) throws SQLException {
		final JdbcTemplate jdbcTemplate=getJdbcTemplate(connectionId);
		if (jdbcTemplate==null) return null;

		return getDatabaseMetaData(jdbcTemplate);
	}
	
	/**
	 * Fetch string values from column (id of column) of resultSet into List 
	 * 
	 * @param resultSet
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	private List<String> getString(ResultSet resultSet, int column) throws SQLException {
		final List<String> out=new Vector<>();
		
		while (resultSet.next()) {
			out.add(resultSet.getString(column));
		}
		
		return out;
	}
	
	@Override
	public List<String> getSchemas(Long connectionId) throws SQLException {
		final DatabaseMetaData databaseMetaData=getDatabaseMetaData(connectionId);
		if (databaseMetaData==null) return Collections.emptyList();
		
		final ResultSet resultSet=databaseMetaData.getSchemas();
		
		return getString(resultSet, 1);
	}
	
	@Override
	public List<String> getTables(Long connectionId) throws SQLException {
		final DatabaseMetaData databaseMetaData=getDatabaseMetaData(connectionId);
		if (databaseMetaData==null) return Collections.emptyList();
		
		final ResultSet resultSet=databaseMetaData.getTables(null, null, "%", new String[] {"TABLE"});
		
		return getString(resultSet, 3);
	}

	private List<String> getColumns(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
		if (databaseMetaData==null) return Collections.emptyList();
		
		final ResultSet resultSet=databaseMetaData.getColumns(null, null, tableName, null);
		
		return getString(resultSet, 4);
	}
	
	@Override
	public List<String> getColumns(Long connectionId, String tableName) throws SQLException {
		final DatabaseMetaData databaseMetaData=getDatabaseMetaData(connectionId);
		return getColumns(databaseMetaData, tableName);
	}

	/**
	 * Construct query to fetch one page. It creates query for database based on mySql (MariaDB etc.)
	 * 
	 * @param columns
	 * @param tableName
	 * @param pageable
	 * @return
	 * @throws SQLException
	 */
	private String getListQuery(List<String> columns, String tableName, Pageable pageable) throws SQLException {
		final StringBuilder sb=new StringBuilder();
		
		sb.append("select ");
		
		int i=0;
		for (final String column : columns) {
			if (i++>0) sb.append(", ");
			sb.append('`').append(column).append('`');
		}
		
		sb.append(" from `").append(tableName).append('`');
		
		if (pageable.getSort()!=null) {
			sb.append(" order by `").append(pageable.getSort()).append('`');
		}
		
		sb.append(" limit ").append(pageable.getOffset()).append(", ").append(pageable.getPageSize());
		
		return sb.toString();
	}
	
	/**
	 * Fetch data on specific page in the list of maps.
	 * 
	 * @param jdbcTemplate
	 * @param tableName
	 * @param pageable
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, Object>> getData(JdbcTemplate jdbcTemplate, String tableName, Pageable pageable) throws SQLException {
		final List<String> columns=getColumns(getDatabaseMetaData(jdbcTemplate), tableName);
		final String query=getListQuery(columns, tableName, pageable);
		
		return jdbcTemplate.queryForList(query);
	}
	
	/**
	 * Construct query to fetch count of records. It creates query for database based on mySql (MariaDB etc.)
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private String getCountQuery(String tableName) throws SQLException {
		final StringBuilder sb=new StringBuilder();
		
		sb.append("select count(*) ");
		sb.append("from `").append(tableName).append('`');

		return sb.toString();
	}
	
	/**
	 * Fetch count of records
	 * 
	 * @param jdbcTemplate
	 * @param tableName
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	private Long getCount(JdbcTemplate jdbcTemplate, String tableName) throws DataAccessException, SQLException {
		return jdbcTemplate.queryForObject(getCountQuery(tableName), Long.class);
	}
	
	@Override
	public ResultListDto getList(Long connectionId, String tableName, PageableDto pageable) throws DataAccessException, SQLException {
		final JdbcTemplate jdbcTemplate=getJdbcTemplate(connectionId);
		if (jdbcTemplate==null) return null;
		
		if (pageable.getSize()==null) {
			pageable.setSize(getCount(jdbcTemplate, tableName));
		}
		
		final ResultListDto out=new ResultListDto(
			pageable,
			getData(jdbcTemplate, tableName, pageable)
		);
		
		return out;
	}

	protected TableStatistics getBasicStatistics(JdbcTemplate jdbcTemplate, String tableName, List<String> columns) {
		// create empty output object
		final TableStatistics tableStatistics=new TableStatistics();
		tableStatistics.setColumnStatistics(new HashMap<String, ColumnStatistic<Object>>());
		
		// construct query (all min, avg and max values in one)
		final StringBuilder sb=new StringBuilder();
		sb.append("select ");
		int i=0;
		for (final String column : columns) {
			if (i++>0) sb.append(", ");
			sb.append("min(`").append(column).append("`), ");
			sb.append("avg(`").append(column).append("`), ");
			sb.append("max(`").append(column).append("`)");
		}
		sb.append(" from `").append(tableName).append('`');
		
		// get values for each column
		final SqlRowSet rs=jdbcTemplate.queryForRowSet(sb.toString());
		if (rs.next()) {
			for (int j=0; j<columns.size(); j++) {
				final String column=columns.get(j);
				final ColumnStatistic<Object> cs=new ColumnStatistic<>();
				cs.setMin(rs.getObject(j*3+1));
				cs.setAvg(rs.getObject(j*3+2));
				cs.setMax(rs.getObject(j*3+3));
				tableStatistics.getColumnStatistics().put(column, cs);
			}
		}
		
		return tableStatistics;
	}
	
	public Object getMedian(JdbcTemplate jdbcTemplate, String tableName, String columnName, Long size) {
		final StringBuilder sb=new StringBuilder();
		sb.append("select `").append(columnName).append("` ");
		sb.append("from `").append(tableName).append("` ");
		sb.append("order by `").append(columnName).append("` ");
		sb.append("limit ").append(size / 2).append(", 1");
		
		return jdbcTemplate.queryForObject(sb.toString(), Object.class);
	}
	
	@Override
	public TableStatistics getStatistics(Long connectionId, String tableName) throws DataAccessException, SQLException {
		final JdbcTemplate jdbcTemplate=getJdbcTemplate(connectionId);
		if (jdbcTemplate==null) return null;
		
		// get columns to statistic fetch
		final List<String> columns=getColumns(connectionId, tableName);
		
		// get basic statistics in one query (min, avg, max)
		final TableStatistics out=getBasicStatistics(jdbcTemplate, tableName, columns);
		
		// get median for each column (only, if any record exists)
		final Long size=getCount(jdbcTemplate, tableName);
		if (size>0) {
			for (final String column : columns) {
				final Object median=getMedian(jdbcTemplate, tableName, column, size);
				out.getColumnStatistics().get(column).setMedian(median);
			}
		}
		
		return out;
	}

}
