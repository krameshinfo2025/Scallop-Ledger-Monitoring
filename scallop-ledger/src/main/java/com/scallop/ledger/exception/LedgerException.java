package com.scallop.ledger.exception;

import org.springframework.http.HttpStatus;

public class LedgerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int status;

	private HttpStatus httpStatus;

	private String code;

	/**
	 * @param status
	 * @param httpStatus
	 * @param code
	 */
	public LedgerException(HttpStatus httpStatus, String code, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	/**
	 * @param status
	 * @param code
	 */
	public LedgerException(int status, String code) {
		this.status = status;
		this.code = code;
	}
	
	

	/**
	 * @param status
	 * @param code
	 */
	public LedgerException(int status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}
	
	

	/**
	 * @param code
	 */
	public LedgerException(String code,String message) {
		super(message);
		this.code = code;
	}

	/**
	 * @return the httpStatus
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	public int status() {
		return status;
	}

	public static LedgerException invalid(String message) {
		return new LedgerException(422, message);
	}

	public static LedgerException conflict(String message) {
		return new LedgerException(409, message);
	}
	
    public static LedgerException notFound(String what) {
        return new LedgerException(HttpStatus.NOT_FOUND, "NOT_FOUND", what + " not found.");
    }

    public static LedgerException conflict(String code, String message) {
        return new LedgerException(HttpStatus.CONFLICT, code, message);
    }

    public static LedgerException badRequest(String code, String message) {
        return new LedgerException(HttpStatus.BAD_REQUEST, code, message);
    }

}
