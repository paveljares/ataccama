package cz.jares.pavel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import cz.jares.pavel.config.orica.MapPageableDtoConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * 
 * @author jaresp
 *
 */
@Configuration
public class OricaConfig {

	@Autowired
	private MapperFactory mapperFactory;
	
	@Bean
	public MapperFactory getMapperFactory() {
		final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		
		// configuration of mapping by class
		final ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new MapPageableDtoConverter());
		
		return mapperFactory;
	}
	
	@Bean
	@Scope("prototype")
	public MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}
	
}
