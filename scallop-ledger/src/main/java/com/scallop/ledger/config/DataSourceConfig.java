/**
 ***********************************************************************************
 DataSourceConfig.Java
 version 1.0
 02,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import java.sql.Connection;
import java.time.Duration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Read/write split between the Postgres writer (primary) and the read replica.
 *
 * <p>
 * Routing is decided per transaction: work inside
 * {@code @Transactional(readOnly = true)} - including the inherited read
 * methods of Spring Data repositories called outside a transaction - runs on
 * the reader; everything else, Flyway included, runs on the writer. A read made
 * inside a read-write transaction stays on the writer, so a service always sees
 * its own writes.
 *
 * <p>
 * {@link LazyConnectionDataSourceProxy} is what makes this work with JPA: it
 * defers fetching the physical connection until the first statement, by which
 * time the transaction manager has marked the connection read-only.
 */
@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {

	/** Connection pool for the Postgres writer, bound from scallop.datasource.writer.* */
	@Bean(destroyMethod = "close")
	@ConfigurationProperties("scallop.datasource.writer")
	public HikariDataSource writerDataSource() {
		return new HikariDataSource();
	}

	/**
	 * Connection pool for the Postgres read replica, bound from
	 * scallop.datasource.reader.*. Its connections should default to read-only
	 * (read-only: true), because the proxy does not switch the flag for it.
	 */
	@Bean(destroyMethod = "close")
	@ConfigurationProperties("scallop.datasource.reader")
	public HikariDataSource readerDataSource() {
		return new HikariDataSource();
	}

	/**
	 * The DataSource that JPA, Flyway and the transaction manager use.
	 *
	 * @param writer   - the writer pool.
	 * @param reader   - the read-replica pool.
	 * @param cooldown - how long reads stay on the writer after the reader fails.
	 */
	@Bean
	@Primary
	public DataSource dataSource(@Qualifier("writerDataSource") DataSource writer,
			@Qualifier("readerDataSource") DataSource reader,
			@Value("${scallop.datasource.reader-fallback-cooldown:PT30S}") Duration cooldown) {

		LazyConnectionDataSourceProxy routing = new LazyConnectionDataSourceProxy();
		routing.setTargetDataSource(writer);
		routing.setReadOnlyDataSource(new ReaderFallbackDataSource(reader, writer, cooldown));

		// Known up front, so the proxy never has to open a connection just to learn them.
		routing.setDefaultAutoCommit(true);
		routing.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		return routing;
	}

}
