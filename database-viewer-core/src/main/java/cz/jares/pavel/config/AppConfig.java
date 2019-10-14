package cz.jares.pavel.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author jaresp
 *
 */
@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableTransactionManagement
@EntityScan("cz.jares.pavel.persistence.entity")
@PropertySource(value = {
	"classpath:META-INF/app.properties",
	"file:database-viewer.properties"
})
@ComponentScan("cz.jares.pavel")
@EnableJpaRepositories(value = "cz.jares.pavel.persistence.repository", transactionManagerRef = "transactionManager" )
@EnableCaching
@EnableSwagger2
public class AppConfig {
	
}
