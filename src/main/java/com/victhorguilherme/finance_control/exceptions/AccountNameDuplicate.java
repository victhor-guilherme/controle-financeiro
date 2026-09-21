package com.victhorguilherme.finance_control.exceptions;

public class AccountNameDuplicate extends RuntimeException{

    public AccountNameDuplicate(String message) {
        super(message);
    }
}
