package cz.jares.pavel.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiControllerTest {
	
	private MockMvc mockMvc;
	
	private ApiController apiController=new ApiController() {
		{
			ReflectionTestUtils.setField(this, "version", "mockedVersion");
		}
	};
	
	@Before
	public void setUp() throws Exception {
		JacksonTester.initFields(this, new ObjectMapper());
		mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();
	}
	
	@Test
	public void testListPeopleInGroup() throws IllegalArgumentException, InvocationTargetException, Exception {
		mockMvc	.perform(get("/api/version"))
				.andExpect(status().isOk())
				.andExpect(content().string("mockedVersion"));
	}
	
}
