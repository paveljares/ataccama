package cz.jares.pavel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import cz.jares.pavel.config.TestContext;
import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.service.impl.ConnectionDataPoolImpl;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={TestContext.class})
public class ConnectionDataPoolTest {

	@Mock
	private ConnectionService connectionService;
	
	@Mock
	private ApplicationContext applicationContext;
	
	@InjectMocks
	private ConnectionDataPool connectionDataPool=new ConnectionDataPoolImpl();
	
	@Test
	public void testUrl() {
		class ConnectionDataPoolImplMock extends ConnectionDataPoolImpl {
			@Override
			public String getUrl(ConnectionDto connectionDto) {
				return super.getUrl(connectionDto);
			}
		};
		
		ConnectionDto c=new ConnectionDto();
		c.setHostname("host");
		c.setDatabaseName("database");
		
		ConnectionDataPoolImplMock mock=new ConnectionDataPoolImplMock();
		assertEquals("jdbc:mysql://host:3306/database", mock.getUrl(c));
		
		c.setPort(123);
		assertEquals("jdbc:mysql://host:123/database", mock.getUrl(c));
	}
	
	@Test
	public void testCreateDataSource() {
		ConnectionDto c=new ConnectionDto();
		c.setHostname("host");
		c.setDatabaseName("database");
		c.setUsername("user");
		c.setPassword("pswd");
		
		DataSource dataSource=connectionDataPool.createDataSource(c);
		assertTrue(dataSource instanceof BasicDataSource);
		assertEquals(1, ((BasicDataSource) dataSource).getInitialSize());
	}
	
	@Test
	public void testManageCache() {
		@SuppressWarnings("unchecked")
		final Map<Long, DataSource> cache=(Map<Long, DataSource>) ReflectionTestUtils.getField(ConnectionDataPoolImpl.class, "DATA_SOURCES");
		cache.clear();
		
		ConnectionDto connection1=new ConnectionDto();
		connection1.setId(1L);
		DataSource ds1=mock(DataSource.class);
		when(connectionService.findOne(1L)).thenReturn(connection1);
		when(applicationContext.getBean("customDataSource", connection1)).thenReturn(ds1);
		
		ConnectionDto connection2=new ConnectionDto();
		connection2.setId(2L);
		DataSource ds2=mock(DataSource.class);
		when(connectionService.findOne(2L)).thenReturn(connection2);
		when(applicationContext.getBean("customDataSource", connection2)).thenReturn(ds2);
		
		when(connectionService.findOne(3L)).thenReturn(null);
		//when(applicationContext.getBean("customDataSource", null)).thenReturn(null);
		
		assertNotNull(connectionDataPool.getJdbcTemplate(1L));
		assertEquals(1, cache.size());
		
		assertNotNull(connectionDataPool.getJdbcTemplate(1L));
		assertEquals(1, cache.size());
		
		assertNotNull(connectionDataPool.getJdbcTemplate(2L));
		assertEquals(2, cache.size());
		
		assertNull(connectionDataPool.getJdbcTemplate(3L));
		assertEquals(2, cache.size());
		
		connectionDataPool.release(1L);
		assertEquals(1, cache.size());
		
		connectionDataPool.release(3L);
		assertEquals(1, cache.size());
		
		connectionDataPool.release(2L);
		assertEquals(0, cache.size());
	}
	
}
