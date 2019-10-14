package cz.jares.pavel.dto;

import lombok.Data;

/**
 * 
 * @author jaresp
 *
 */
@Data
public class ConnectionDto {
	
	private Long id;
	private String name;
	private String hostname;
	private Integer port;
	private String databaseName;
	private String username;
	private String password;
	
}
