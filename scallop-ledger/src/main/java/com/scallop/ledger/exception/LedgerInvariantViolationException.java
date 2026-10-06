package com.scallop.ledger.exception;

public class LedgerInvariantViolationException extends LedgerException {
  private static final long serialVersionUID = 1L;

  public LedgerInvariantViolationException(String message) {
    super(422, message);
  }
}
