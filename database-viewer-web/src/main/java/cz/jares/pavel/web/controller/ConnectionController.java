package cz.jares.pavel.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.service.ConnectionService;

/**
 * 
 *  This controller is responsible for managing of connection.
 *  Web application can create, update or delete one connection parameters and
 * it can also fetch list of all stored connections.
 * 
 * @author jaresp
 *
 */
@Controller
@RequestMapping("/connection")
public class ConnectionController {

	@Autowired
	private ConnectionService connectionService;
	
	/**
	 * Get all stored connections. 
	 * 
	 * @return list of existing connections
	 */
	@RequestMapping(
		method=RequestMethod.GET, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public List<ConnectionDto> getAll() {
		return connectionService.getAll();
	}
	
	/**
	 * Create a new connection (store in the database)
	 * 
	 * @param connection
	 * @return DTO with new connection's parameters
	 */
	@RequestMapping(
		method={RequestMethod.POST}, 
		consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public ConnectionDto create(@RequestBody ConnectionDto connection) {
		return connectionService.create(connection);
	}
	
	/**
	 * Update parameters about specific connection
	 * 
	 * @param connection - id of connection
	 * @return DTO with updated connections DTO
	 */
	@RequestMapping(
		method={RequestMethod.PUT}, 
		consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, 
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseBody
	public ConnectionDto update(@RequestBody ConnectionDto connection) {
		return connectionService.update(connection);
	}
	
	/**
	 * Delete one connection in database by ID
	 * 
	 * @param id - id of connection in database
	 */
	@RequestMapping(
		value="/{id}",
		method=RequestMethod.DELETE
	)
	public void delete(@PathVariable("id") Long id) {
		connectionService.delete(id);
	}
	
}
