package cz.jares.pavel.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * 
 * @author jaresp
 *
 */
@Configuration
public class JpaConfig {
	
	@Value("${datasource.url}")
	private String dataSourceUrl;
	
	@Value("${datasource.username}")
	private String dataSourceUser;
	
	@Value("${datasource.password}")
	private String dataSourcePassword;

	@Bean
	@Qualifier("jpa")
	@Primary
	public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
	    dataSource.setUsername(dataSourceUser);
	    dataSource.setPassword(dataSourcePassword);
	    dataSource.setUrl(dataSourceUrl); 
	     
	    return dataSource;
	}
	
	private Properties getAdditionalProperties() {
	    Properties properties = new Properties();
	    properties.setProperty("hibernate.hbm2ddl.auto", "update");
	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
	        
	    return properties;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] {"cz.jares.pavel.persistence.entity"});

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(getAdditionalProperties());

		return em;
	}
	
	@Bean("transactionManager")
	public JpaTransactionManager getTransactionManager(@Autowired EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean
	public JdbcTemplate getJdbcTemplate(@Autowired DataSource dataSource) {
		final JdbcTemplate out=new JdbcTemplate();
		out.setDataSource(dataSource);
		return out;
	}
	
}
