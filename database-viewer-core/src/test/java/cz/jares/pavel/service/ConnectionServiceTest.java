package cz.jares.pavel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cz.jares.pavel.config.TestContext;
import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.persistence.entity.Connection;
import cz.jares.pavel.persistence.repository.ConnectionRepository;
import cz.jares.pavel.utils.ObjectUtils;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={TestContext.class})
@Transactional
public class ConnectionServiceTest {

	@Autowired
	private ConnectionRepository connectionRepository;
	
	@Autowired
	private ConnectionService connectionService;
	
	private List<Long> ids=new Vector<>();
	
	@Before
	public void initData() {
		connectionRepository.deleteAll();
		
		for (int i=0; i<10; i++) {
			Connection c=ObjectUtils.getConnection(i);
			connectionRepository.save(c);
			ids.add(c.getId());
		}
	}
	
	@Test
	public void testSaveDelete() {
		assertNull(connectionService.create(null));
		assertNull(connectionService.update(null));
		
		ConnectionDto c=new ConnectionDto();
		c.setName("testName");
		c.setHostname("testHostname");
		c.setDatabaseName("testDatabaseName");
		c.setUsername("testUser");
		c.setPassword("testPass");
		c.setPort(12345);
		c=connectionService.create(c);
		
		assertEquals("testName", c.getName());
		assertEquals("testHostname", c.getHostname());
		assertEquals("testDatabaseName", c.getDatabaseName());
		assertEquals("testUser", c.getUsername());
		assertEquals("testPass", c.getPassword());
		assertEquals(new Integer(12345), c.getPort());
		
		c.setName("testName2");
		c.setHostname("testHostname2");
		c.setDatabaseName("testDatabaseName2");
		c.setUsername("testUser2");
		c.setPassword("testPass2");
		c.setPort(12346);
		
		c=connectionService.update(c);
		
		assertEquals("testName2", c.getName());
		assertEquals("testHostname2", c.getHostname());
		assertEquals("testDatabaseName2", c.getDatabaseName());
		assertEquals("testUser2", c.getUsername());
		assertEquals("testPass2", c.getPassword());
		assertEquals(new Integer(12346), c.getPort());
		
		assertTrue(connectionRepository.existsById(c.getId()));
		
		long before=connectionRepository.count();
		connectionService.delete(c.getId());
		assertEquals(before-1, connectionRepository.count());
		
		assertFalse(connectionRepository.existsById(c.getId()));
	}
	
	private void assertEqualsData(Connection c1, ConnectionDto c2) {
		assertEquals(c1.getName(), c2.getName());
		assertEquals(c1.getHostname(), c2.getHostname());
		assertEquals(c1.getDatabaseName(), c2.getDatabaseName());
		assertEquals(c1.getPort(), c2.getPort());
		assertEquals(c1.getUsername(), c2.getUsername());
		assertEquals(c1.getPassword(), c2.getPassword());
	}
	
	@Test
	public void testFind() {
		Connection c1=ObjectUtils.getConnection(0);
		ConnectionDto c2=connectionService.findOne(ids.get(0));
		assertEqualsData(c1, c2);
		
		assertNull(connectionService.findOne(-123L));
	}
	
	@Test
	@After
	public void testAll() {
		List<ConnectionDto> cs=connectionService.getAll();
		assertNotNull(cs);
		
		Collections.sort(cs, (ConnectionDto a, ConnectionDto b) -> a.getId().compareTo(b.getId()));
		
		assertEquals(10, cs.size());
		for (int i=0; i<cs.size(); i++) {
			// to have the same ID
			Connection c1=ObjectUtils.getConnection(i);
			ConnectionDto c2=cs.get(i);
			assertEqualsData(c1, c2);
		}
	}
	
}
