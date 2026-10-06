/**
 ***********************************************************************************
 LedgerApplication.Java
 version 1.0
 24,October,2026
 
 Copyright © 2026 Scallop Group. 
 All Rights Reserved
 ***********************************************************************************
 */

package com.scallop.ledger;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class LedgerApplication implements Serializable {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = -3866851854182689326L;

	/** The Place Holder for Logger */
	private static final Logger LOGGER = LogManager.getLogger(LedgerApplication.class);

	/** The Place Holder environment for Environment */
	@Autowired
	private transient Environment environment;

	/**
	 * @param environment
	 */
	public LedgerApplication(Environment environment) {
		super();
		this.environment = environment;
	}

	/**
	 * 
	 * <p/>
	 * Spring profiles can be configured with a program arguments
	 * --spring.profiles.active=your-active-profile
	 * <p/>
	 *
	 * @throws java.io.IOException if any.
	 */
	@PostConstruct
	public void initApplication() throws IOException {

		final String[] activeProfiles = environment.getActiveProfiles();

		final int length = activeProfiles.length;

		if (length == 0) {

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("No Spring profile configured, running with default configuration");
			}

		} else {

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Running with Spring profile(s) : {}", Arrays.toString(activeProfiles));
			}

		}
	}

	/**
	 * Main method, used to run the application.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * 
	 * @throws java.net.UnknownHostException if any.
	 */
	public static void main(final String[] args) throws UnknownHostException {

		ConfigurableApplicationContext app = new SpringApplicationBuilder(LedgerApplication.class).properties(props())
				.run(args);
		final Environment environment = app.getEnvironment();
		if (LOGGER.isInfoEnabled()) {

			LOGGER.info(
					"Access URLs:\n----------------------------------------------------------\n\t"
							+ "Local: \t\thttp://127.0.0.1:{}\n\t"
							+ "External: \thttp://{}:{}\n----------------------------------------------------------",
					environment.getProperty("server.port"), InetAddress.getLocalHost().getHostAddress(),
					environment.getProperty("server.port"));
		}

	}

	private static Properties props() throws UnknownHostException {
		Properties properties = new Properties();
		properties.setProperty("eureka.instance.hostname", InetAddress.getLocalHost().getHostAddress() + ":5000");
		properties.setProperty("hostname", InetAddress.getLocalHost().getHostAddress());
		properties.setProperty("eureka.client.serviceUrl.defaultZone",
				"http://" + InetAddress.getLocalHost().getHostAddress() + ":5000" + "/eureka/");
		return properties;
	}

}
