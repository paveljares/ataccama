package cz.jares.pavel.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.jares.pavel.dto.PageableDto;
import cz.jares.pavel.dto.ResultListDto;
import cz.jares.pavel.dto.TableStatistics;
import cz.jares.pavel.service.DatabaseService;
import ma.glasnost.orika.MapperFacade;

/**
 * 
 *  This controller is responsible for making base operations on database / table. All operations
 * are identified by connectionId {@link ConnectionController}.
 *  
 *  Controller offer listening of schemas, tables and columns of table. 
 * 
 * @author jaresp
 *
 */
@Controller
@RequestMapping("/database")
public class DatabaseController {

	@Autowired
	private MapperFacade mapper;
	
	@Autowired
	private DatabaseService databaseService;
	
	/**
	 * Fetch names of schemas
	 * 
	 * @param connectionId - database conection's ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value="/{connectionId}/schemas", 
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public List<String> getSchemas(@PathVariable("connectionId") Long connectionId) throws Exception {
		return databaseService.getSchemas(connectionId);
	}
	
	/**
	 * Fetch list of existing table's names in database
	 * 
	 * @param connectionId - database conection's ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value="/{connectionId}/tables", 
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public List<String> getTables(@PathVariable("connectionId") Long connectionId) throws Exception {
		return databaseService.getTables(connectionId);
	}
	
	/**
	 *  Fetch names of columns in table specified by tableName parameter, located in database
	 * identified by connectionId.
	 * 
	 * @param connectionId - database conection's ID
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value="/{connectionId}/{tableName}/columns", 
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public List<String> getColumns(
		@PathVariable("connectionId") Long connectionId, 
		@PathVariable("tableName") String tableName
	) throws Exception {
		return databaseService.getColumns(connectionId, tableName);
	}
	
	/**
	 *  This method allow fetch data from specific table. Table is identified by connectionId 
	 * and table name. The result use pagging.
	 *  Data in response are stored in list of maps (key=column name, value=value in table for
	 * specific row).
	 *  Returned object contains also pageable object ({@see ResultListDto#getPageable()}). This
	 * object contains count of all records in the table ({@see PageableDto#getSize()})
	 * 
	 * @param connectionId
	 * @param tableName
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value="/{connectionId}/{tableName}/list", 
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public ResultListDto getList(
		@PathVariable("connectionId") Long connectionId, 
		@PathVariable("tableName") String tableName, 
		@RequestParam Map<String, String> parameters
	) throws Exception {
		final PageableDto pageable=mapper.map(parameters, PageableDto.class);
		
		return databaseService.getList(connectionId, tableName, pageable);
	}

	/**
	 *  This method fetch statistic (min, max, avg, median) for each attribute in table
	 * which is specified by connectionId (database) and table name.
	 * 
	 * @param connectionId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(
		value="/{connectionId}/{tableName}/statistics", 
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public TableStatistics getStatistics(
		@PathVariable("connectionId") Long connectionId, 
		@PathVariable("tableName") String tableName
	) throws Exception {
		return databaseService.getStatistics(connectionId, tableName);
	}
	
}
