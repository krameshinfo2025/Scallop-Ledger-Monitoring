package com.scallop.ledger.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiErrors {
  @ExceptionHandler(LedgerException.class)
  ResponseEntity<?> ledger(LedgerException e) {
    return ResponseEntity.status(e.status()).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler({
    org.springframework.dao.DataAccessException.class,
    org.springframework.transaction.TransactionException.class
  })
  ResponseEntity<?> database(Exception e) {
    return ResponseEntity.status(409)
        .body(
            Map.of(
                "error",
                "Database invariant, duplicate reference or transaction conflict; retry only with"
                    + " the same idempotency key"));
  }

  @ExceptionHandler({
    org.springframework.web.bind.MethodArgumentNotValidException.class,
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<?> input(Exception e) {
    return ResponseEntity.badRequest().body(Map.of("error", "Invalid request fields or format"));
  }
}
