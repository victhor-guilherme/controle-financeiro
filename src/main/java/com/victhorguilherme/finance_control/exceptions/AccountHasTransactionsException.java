package com.victhorguilherme.finance_control.exceptions;

public class AccountHasTransactionsException extends RuntimeException {
    public AccountHasTransactionsException(String message) {
        super(message);
    }
}
