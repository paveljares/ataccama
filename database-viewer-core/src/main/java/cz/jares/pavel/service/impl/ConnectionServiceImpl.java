package cz.jares.pavel.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.jares.pavel.dto.ConnectionDto;
import cz.jares.pavel.persistence.entity.Connection;
import cz.jares.pavel.persistence.repository.ConnectionRepository;
import cz.jares.pavel.service.ConnectionService;
import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author jaresp
 *
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
	
	private MapperFacade mapper;
	
	@Autowired
	private ConnectionRepository connectionRepository;

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public List<ConnectionDto> getAll() {
		final List<Connection> all=connectionRepository.findAll();
		final List<ConnectionDto> out=mapper.mapAsList(all, ConnectionDto.class);
		return out;
	}

	@Override
	public ConnectionDto create(ConnectionDto connectionDto) {
		if (connectionDto==null) return null;
		
		Connection connection=mapper.map(connectionDto, Connection.class);
		connection=connectionRepository.save(connection);
		
		return mapper.map(connection, ConnectionDto.class);
	}
	
	@Override
	public ConnectionDto update(ConnectionDto connectionDto) {
		if (connectionDto==null) return null;
		
		Connection connection=connectionRepository.findById(connectionDto.getId()).orElseGet(Connection::new);
		mapper.map(connectionDto, connection);
		
		connection=connectionRepository.save(connection);
		
		return mapper.map(connection, ConnectionDto.class);
	}

	@Override
	public void delete(Long id) {
		connectionRepository.deleteById(id);
	}

	@Override
	public ConnectionDto findOne(Long id) {
		final Connection connection=connectionRepository.findById(id).orElse(null);
		if (connection==null) return null;
		return mapper.map(connection, ConnectionDto.class);
	}

}
