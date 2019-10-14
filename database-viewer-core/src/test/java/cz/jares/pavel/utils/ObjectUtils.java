package cz.jares.pavel.utils;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.persistence.entity.Connection;

/**
 * 
 * @author jaresp
 *
 */
public final class ObjectUtils {

	private ObjectUtils() {
	}
	
	public static ConnectionDto getConnectionDto(int i) {
		ConnectionDto out=new ConnectionDto();
		out.setId((long) i);
		out.setDatabaseName("database" + i);
		out.setName("name" + i);
		out.setHostname("hostname" + i);
		out.setPort(1000+i);
		out.setUsername("user" + i);
		out.setPassword("paswd" + i);
		return out;
	}
	
	public static Connection getConnection(int i) {
		final Connection out=new Connection();
		out.setName("name" + i);
		out.setHostname("host" + i);
		out.setUsername("user" + i);
		out.setPassword("pass" + i);
		out.setPort(i);
		return out;
	}
	
}
