package cz.jares.pavel.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.service.ConnectionDataPool;
import cz.jares.pavel.service.ConnectionService;

/**
 * 
 * @author jaresp
 *
 */
@Configuration
public class ConnectionDataPoolImpl implements ConnectionDataPool {

	private static final Map<Long, DataSource> DATA_SOURCES=Collections.synchronizedMap(new HashMap<>());
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ConnectionService connectionService;
	
	@Value("${datasource.driverClassName}")
	private String driverClassName;
	
	/**
	 * Construct connection string for connection in parameter
	 * 
	 * @param connectionDto - DTO with parameters to create connection string
	 * @return URL - connection string
	 */
	protected String getUrl(ConnectionDto connectionDto) {
		final StringBuilder sb=new StringBuilder();
		
		sb.append("jdbc:mysql://");
		sb.append(connectionDto.getHostname());
		sb.append(':');
		sb.append(connectionDto.getPort()==null?3306:connectionDto.getPort());
		sb.append('/');
		sb.append(connectionDto.getDatabaseName());
		
		return sb.toString();
	}
	
	@Bean("customDataSource")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Qualifier("custom")
	@Override
	public DataSource createDataSource(ConnectionDto connectionDto) {
		BasicDataSource out=new BasicDataSource();
		out.setDriverClassName(driverClassName);
		out.setUrl(getUrl(connectionDto));
		out.setUsername(connectionDto.getUsername());
		out.setPassword(connectionDto.getPassword());
		out.setInitialSize(1);
		return out;
	}
	
	protected DataSource getDataSource(Long id) {
		// if datasource is in the cache, return it immediatelly
		DataSource out=DATA_SOURCES.get(id);
		if (out!=null) return out;
		
		// lock thread (to avoit paraller creating of databasource)
		synchronized (DATA_SOURCES) {
			out=DATA_SOURCES.get(id);
			if (out!=null) return out;
			
			// datasouce has not been created yet
			
			// take data about connection
			final ConnectionDto dto=connectionService.findOne(id);
			if (dto==null) return null;
			
			// create datasource
			out=(DataSource) applicationContext.getBean("customDataSource", dto);
			if (out==null) return null;
			
			// store in the cache
			DATA_SOURCES.put(id,  out);
		}
		
		return out;
	}
	
	@Bean("customJdbcTemplate")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Override
	public JdbcTemplate getJdbcTemplate(Long id) {
		final DataSource dataSource=getDataSource(id);
		if (dataSource==null) return null;
		
		final JdbcTemplate out=new JdbcTemplate();
		out.setDataSource(dataSource);
		return out;
	}
	
	@Override
	public void release(Long connectionId) {
		DATA_SOURCES.remove(connectionId);
	}
	
}
