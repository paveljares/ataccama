package cz.jares.pavel.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cz.jares.pavel.config.OricaConfig;
import cz.jares.pavel.dto.ColumnStatistic;
import cz.jares.pavel.dto.PageableDto;
import cz.jares.pavel.dto.ResultListDto;
import cz.jares.pavel.dto.TableStatistics;
import cz.jares.pavel.service.DatabaseService;
import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author jaresp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={OricaConfig.class})
public class DatabaseControllerTest {

	private MockMvc mockMvc;
		
	@Autowired
	private MapperFacade mapper;
	
	private DatabaseController databaseController=new DatabaseController() {
		
		private Map<String, Object> getRow(String...data) {
			Map<String, Object> out=new HashMap<>();
			for (int i=0; i<data.length/2; i++) {
				out.put(data[i*2], data[i*2+1]);
			}
			return out;
		}
		
		{
			try {
				DatabaseService databaseService=mock(DatabaseService.class);
				
				when(databaseService.getSchemas(1L)).thenReturn(Arrays.asList("schemaA", "schemaB"));
				when(databaseService.getTables(1L)).thenReturn(Arrays.asList("tableA", "tableB"));
				when(databaseService.getColumns(1L, "tableA")).thenReturn(Arrays.asList("columnA", "columnB"));
				
				PageableDto p=new PageableDto(10,2);
				p.setSize(23L);
				List<Map<String, Object>> data=new LinkedList<>();
				data.add(getRow("column1", "val1", "column2", "val2"));
				data.add(getRow("column1", "val3", "column2", "val4"));
				
				ResultListDto rl=new ResultListDto(p, data);
				
				when(databaseService.getList(1L, "table", new PageableDto(2,3)))
					.thenReturn(rl);
				
				TableStatistics ts=new TableStatistics();
				Map<String, ColumnStatistic<Object>> map=new HashMap<>();
				map.put("col1", new ColumnStatistic<>(1, 3, 4, 2));
				map.put("col2", new ColumnStatistic<>("a", 0.0, "z", "f"));
				ts.setColumnStatistics(map);
				when(databaseService.getStatistics(1L, "table")).thenReturn(ts);
				
				ReflectionTestUtils.setField(this, "databaseService", databaseService);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	};
	
	@Before
	public void setUp() throws Exception {
		JacksonTester.initFields(this, new ObjectMapper());
		mockMvc = MockMvcBuilders.standaloneSetup(databaseController).build();
		
		ReflectionTestUtils.setField(databaseController, "mapper", mapper);
	}
	
	@Test
	public void testMethods() throws Exception {
		mockMvc	.perform(get("/database/1/schemas"))
				.andExpect(status().isOk())
				.andExpect(content().json("[\"schemaA\",\"schemaB\"]"));
		
		mockMvc	.perform(get("/database/1/tables"))
				.andExpect(status().isOk())
				.andExpect(content().json("[\"tableA\",\"tableB\"]"));

		mockMvc	.perform(get("/database/1/tableA/columns"))
				.andExpect(status().isOk())
				.andExpect(content().json("[\"columnA\",\"columnB\"]"));
		
		mockMvc	.perform(get("/database/1/table/list?page=3&size=2"))
				.andExpect(status().isOk())
				.andExpect(content().json(
			"{" +
				"\"pageable\" : {" +
					"\"size\" : 23" +
				"}," +
				"\"items\" : [" +
					"{\"column1\":\"val1\",\"column2\":\"val2\"}," +
					"{\"column1\":\"val3\",\"column2\":\"val4\"}" +
				"]" +
			"}"
		));
		
		mockMvc	.perform(get("/database/1/table/statistics"))
				.andExpect(status().isOk())
				.andExpect(content().json(
			"{" +
				"\"column\" : {" +
					"\"col1\" : {" +
						"\"min\" : 1," +
						"\"avg\" : 3," +
						"\"max\" : 4," +
						"\"median\" : 2" +
					"}," +
					"\"col2\" : {" +
						"\"min\" : \"a\"," +
						"\"avg\" : 0.0," +
						"\"max\" : \"z\"," +
						"\"median\" : \"f\"" +
					"}" +
				"}" +
			"}"
		));
		
	}
	
}
