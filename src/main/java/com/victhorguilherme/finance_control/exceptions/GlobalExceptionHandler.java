package com.victhorguilherme.finance_control.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<String> tratarRecursoNaoEncontrado(ResourceNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

    }

    @ExceptionHandler(AccountNameDuplicate.class)
    public ResponseEntity<String> handleDuplicateNames(AccountNameDuplicate ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryNameDuplicate.class)
    public ResponseEntity<String> handleDuplicateCategories(CategoryNameDuplicate ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPeriodException.class)
    public ResponseEntity<String> handleInvalidPeriod(InvalidPeriodException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccountHasTransactionsException.class)
    public ResponseEntity<String> handleAccountHasTransactions(AccountHasTransactionsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}