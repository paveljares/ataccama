package cz.jares.pavel.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author jaresp
 *
 */
@Configuration
@EnableTransactionManagement
@EntityScan(value = "cz.jares.pavel.persistence.entity")
@ComponentScan(value = "cz.jares.pavel", excludeFilters= {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AppConfig.class})
})
@PropertySource({
	"classpath:META-INF/app.properties",
	"classpath:META-INF/database-viewer.properties"
})
@EnableJpaRepositories(value = "cz.jares.pavel.persistence.repository", transactionManagerRef = "transactionManager" )
public class TestContext {
	
}
