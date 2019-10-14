package cz.jares.pavel;

import org.springframework.boot.SpringApplication;

import cz.jares.pavel.config.AppConfig;

/**
 * 
 * @author jaresp
 * 
 * 	Main class to start embeded Tomcat and publish webservices.
 *
 */

public class Application {

	public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

}
