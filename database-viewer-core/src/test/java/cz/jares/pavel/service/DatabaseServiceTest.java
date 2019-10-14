package cz.jares.pavel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.h2.tools.SimpleResultSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cz.jares.pavel.config.TestContext;
import cz.jares.pavel.dto.ColumnStatistic;
import cz.jares.pavel.dto.PageableDto;
import cz.jares.pavel.dto.ResultListDto;
import cz.jares.pavel.dto.TableStatistics;
import cz.jares.pavel.service.impl.DatabaseServiceImpl;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={TestContext.class})
public class DatabaseServiceTest {

	@InjectMocks
	private DatabaseService databaseService=new DatabaseServiceImpl();
	
	@Mock
	private ApplicationContext applicationContext;
	
	private JdbcTemplate[] jdbcTemplates;
	
	private List<String> getStringList(String prefix, int count) {
		final List<String> out=new ArrayList<String>(count);
		for (int i=0; i<count; i++) {
			out.add(prefix + i);
		}
		return out;
	}
	
	private ResultSet getResultList(String prefix, int count, int column) {
		SimpleResultSet out=new SimpleResultSet();
		for (int i=0; i<column; i++) {
			out.addColumn("column"+column, Types.VARCHAR, 50, 0);
		}
		
		for (final String value : getStringList(prefix, count)) {
			Object[] row=new Object[column];
			row[column-1]=value;
			out.addRow(row);
		}
		
		return out;
	}
	
	private JdbcTemplate constructJdbcTemplate(int i) throws SQLException {
		JdbcTemplate jdbcTemplate=mock(JdbcTemplate.class);
		DataSource dataSource=mock(DataSource.class);
		Connection connection=mock(Connection.class);
		DatabaseMetaData metadata=mock(DatabaseMetaData.class);
		
		when(connection.getMetaData()).thenReturn(metadata);
		when(dataSource.getConnection()).thenReturn(connection);
		when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		when(applicationContext.getBean("customJdbcTemplate", Long.valueOf(i))).thenReturn(jdbcTemplate);
		
		when(metadata.getSchemas()).thenReturn(getResultList("schema", i, 1));
		when(metadata.getTables(null, null, "%", new String[] {"TABLE"})).thenReturn(getResultList("table", i, 3));
		when(metadata.getColumns(null, null, "table", null)).thenReturn(getResultList("column", i, 4));
		
		return jdbcTemplate;
	}
	
	@Before
	public void init() throws SQLException {
		jdbcTemplates=new JdbcTemplate[3];
		for (int i=0; i<3; i++) {
			jdbcTemplates[i]=constructJdbcTemplate(i);
		}
	}
	
	@Test
	public void testStructure() throws SQLException {		
		for (int i=0; i<3; i++) {
			assertEquals(getStringList("schema", i), databaseService.getSchemas((long) i));
			assertEquals(getStringList("table", i), databaseService.getTables((long) i));
			assertEquals(getStringList("column", i), databaseService.getColumns((long) i, "table"));
		}
	}
	
	@Test
	public void testCount() throws DataAccessException, SQLException {
		when(jdbcTemplates[2].queryForObject("select count(*) from `table`", Long.class)).thenReturn(55L);
		
		List<Map<String, Object>> data=new Vector<Map<String, Object>>();
		for (int i=0; i<10; i++) {
			Map<String, Object> row=new HashMap<String, Object>();
			row.put("column0", "value_0_"+i);
			row.put("column1", "value_1_"+i);
			data.add(row);
		}
		
		when(jdbcTemplates[2].queryForList("select `column0`, `column1` from `table` limit 50, 10")).thenReturn(data);
		
		ResultListDto rl=databaseService.getList(2L, "table", new PageableDto(10, 5));
		assertNotNull(rl);
		assertNotNull(rl.getItems());
		assertEquals(10, rl.getItems().size());
		assertNotNull(rl.getPageable());
		assertEquals(Long.valueOf(55), rl.getPageable().getSize());
		assertEquals(50, rl.getPageable().getOffset());
		assertEquals(5, rl.getPageable().getPageNumber());
		assertEquals(10, rl.getPageable().getPageSize());
	}
	
	@Test
	public void testStatistics() throws DataAccessException, SQLException {
		SqlRowSet srs=mock(SqlRowSet.class);
		when(srs.next()).thenReturn(true);
		when(srs.getObject(1)).thenReturn(1);
		when(srs.getObject(2)).thenReturn(2);
		when(srs.getObject(3)).thenReturn(3);
		when(srs.getObject(4)).thenReturn(5);
		when(srs.getObject(5)).thenReturn(6);
		when(srs.getObject(6)).thenReturn(7);
		
		when(jdbcTemplates[2].queryForRowSet(
			"select " +
				"min(`column0`), avg(`column0`), max(`column0`), " +
				"min(`column1`), avg(`column1`), max(`column1`) " +
			"from `table`")).thenReturn(srs);
		
		when(jdbcTemplates[2].queryForObject("select count(*) from `table`", Long.class)).thenReturn(5L);
		
		when(jdbcTemplates[2].queryForObject("select `column0` from `table` order by `column0` limit 2, 1", Object.class))
			.thenReturn(8);
		when(jdbcTemplates[2].queryForObject("select `column1` from `table` order by `column1` limit 2, 1", Object.class))
			.thenReturn(9);
		
		TableStatistics ts=databaseService.getStatistics(2L, "table");
		
		assertNotNull(ts);
		assertNotNull(ts.getColumnStatistics());
		assertEquals(2, ts.getColumnStatistics().size());
		
		ColumnStatistic<Object> cs1=ts.getColumnStatistics().get("column0");
		ColumnStatistic<Object> cs2=ts.getColumnStatistics().get("column1");
		assertNotNull(cs1);
		assertNotNull(cs2);
		
		assertEquals(1, cs1.getMin());
		assertEquals(2, cs1.getAvg());
		assertEquals(3, cs1.getMax());
		assertEquals(8, cs1.getMedian());
		
		assertEquals(5, cs2.getMin());
		assertEquals(6, cs2.getAvg());
		assertEquals(7, cs2.getMax());
		assertEquals(9, cs2.getMedian());
		
	}
	
}
