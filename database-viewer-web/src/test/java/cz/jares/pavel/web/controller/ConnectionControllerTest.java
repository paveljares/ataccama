package cz.jares.pavel.web.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.service.ConnectionService;
import cz.jares.pavel.utils.ObjectUtils;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ConnectionControllerTest {

	private MockMvc mockMvc;

	private Set<Long> deleted=new HashSet<>();
	
	private ConnectionController connectionController=new ConnectionController() {
		
		int sequence=3;
		
		{
			ConnectionService connectionService=mock(ConnectionService.class);
			
			List<ConnectionDto> all=Arrays.asList(ObjectUtils.getConnectionDto(1), ObjectUtils.getConnectionDto(2));
			when(connectionService.getAll()).thenReturn(all);
			
			doAnswer(new Answer<ConnectionDto>() {
				@Override
				public ConnectionDto answer(InvocationOnMock invocation) throws Throwable {
					final ConnectionDto c=(ConnectionDto) invocation.getArguments()[0];
					c.setId((long) sequence++);
					return c;
				}
			}).when(connectionService).create(Mockito.any());
			
			doAnswer(new Answer<ConnectionDto>() {
				@Override
				public ConnectionDto answer(InvocationOnMock invocation) throws Throwable {
					return (ConnectionDto) invocation.getArguments()[0];
				}
			}).when(connectionService).update(Mockito.any());
			
			ReflectionTestUtils.setField(this, "connectionService", connectionService);
		}
		
		public void delete(@PathVariable("id") Long id) {
			deleted.add(id);
		};
	};
	
	@Before
	public void setUp() throws Exception {
		JacksonTester.initFields(this, new ObjectMapper());
		mockMvc = MockMvcBuilders.standaloneSetup(connectionController).build();
	}
	
	@Test
	public void testMethods() throws IllegalArgumentException, InvocationTargetException, Exception {
		mockMvc	.perform(get("/connection"))
				.andExpect(status().isOk())
				.andExpect(content().json(
			"[" + 
				"{\"id\":1, \"databaseName\":\"database1\", \"name\":\"name1\", \"hostname\":\"hostname1\", \"port\":1001, \"username\":\"user1\", \"password\":\"paswd1\"}," +
				"{\"id\":2, \"databaseName\":\"database2\", \"name\":\"name2\", \"hostname\":\"hostname2\", \"port\":1002, \"username\":\"user2\", \"password\":\"paswd2\"}" +
			"]"
		));
		
		mockMvc	.perform(post("/connection")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(
						"{\"databaseName\":\"database3\", \"name\":\"name3\", \"hostname\":\"hostname3\", \"port\":1003, \"username\":\"user3\", \"password\":\"paswd3\"}"
					)
				)
				.andExpect(status().isOk())
				.andExpect(content().json(
					"{\"id\":3, \"databaseName\":\"database3\", \"name\":\"name3\", \"hostname\":\"hostname3\", \"port\":1003, \"username\":\"user3\", \"password\":\"paswd3\"}"
				));
		
		mockMvc	.perform(put("/connection")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(
						"{\"id\":2, \"databaseName\":\"database3\", \"name\":\"name3\", \"hostname\":\"hostname3\", \"port\":1003, \"username\":\"user3\", \"password\":\"paswd3\"}"
					)
				)
				.andExpect(status().isOk())
				.andExpect(content().json(
					"{\"id\":2, \"databaseName\":\"database3\", \"name\":\"name3\", \"hostname\":\"hostname3\", \"port\":1003, \"username\":\"user3\", \"password\":\"paswd3\"}"
				));
		
		assertEquals(0, deleted.size());
		mockMvc	.perform(delete("/connection/3"))
				.andExpect(status().isOk());
		assertEquals(Collections.singleton(3L), deleted);
	}
	
}
