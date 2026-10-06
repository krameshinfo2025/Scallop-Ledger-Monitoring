/**
 ***********************************************************************************
 ReaderFallbackDataSource.Java
 version 1.0
 02,October,2026

 Copyright © 2026 Scallop Group.
 All Rights Reserved
 ***********************************************************************************
 */
package com.scallop.ledger.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Read-replica DataSource that falls back to the writer when no reader
 * connection can be obtained.
 *
 * <p>
 * After a failure, reads go straight to the writer for a cooldown period
 * instead of waiting out the reader's connection timeout on every request; the
 * reader is tried again once the cooldown has passed. Writer connections handed
 * out here are marked read-only so a read-only transaction still cannot write.
 *
 * <p>
 * Only connection acquisition is covered: a query that fails on an already
 * obtained reader connection is reported to the caller as usual.
 */
public class ReaderFallbackDataSource extends DelegatingDataSource {

	/** The Place Holder for Logger */
	private static final Logger LOGGER = LogManager.getLogger(ReaderFallbackDataSource.class);

	/** The Place Holder for writer of type DataSource */
	private final DataSource writer;

	/** The Place Holder for cooldownMillis of type long */
	private final long cooldownMillis;

	/** Epoch millis until which the reader is skipped; 0 while the reader is healthy. */
	private volatile long readerSkippedUntil;

	/**
	 * @param reader   - the read-replica DataSource.
	 * @param writer   - the writer DataSource used while the reader is unavailable.
	 * @param cooldown - how long to skip the reader after it fails.
	 */
	public ReaderFallbackDataSource(DataSource reader, DataSource writer, Duration cooldown) {
		super(reader);
		this.writer = writer;
		this.cooldownMillis = cooldown.toMillis();
	}

	@Override
	public Connection getConnection() throws SQLException {

		if (System.currentTimeMillis() < readerSkippedUntil) {
			return readOnlyWriterConnection();
		}

		try {
			Connection connection = super.getConnection();
			if (readerSkippedUntil != 0) {
				readerSkippedUntil = 0;
				LOGGER.info("Read replica is reachable again; read-only work is back on the reader.");
			}
			return connection;

		} catch (SQLException | RuntimeException ex) {
			// RuntimeException covers Hikari's PoolInitializationException when the
			// pool cannot start at all.
			readerSkippedUntil = System.currentTimeMillis() + cooldownMillis;
			LOGGER.warn("Read replica unavailable ({}); sending read-only work to the writer for {} ms.",
					ex.getMessage(), cooldownMillis);
			return readOnlyWriterConnection();
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException("Credentials come from the pool configuration.");
	}

	private Connection readOnlyWriterConnection() throws SQLException {

		Connection connection = writer.getConnection();
		try {
			// Hikari restores the pool default when the connection is returned.
			connection.setReadOnly(true);
		} catch (SQLException ex) {
			connection.close();
			throw ex;
		}
		return connection;
	}

}
