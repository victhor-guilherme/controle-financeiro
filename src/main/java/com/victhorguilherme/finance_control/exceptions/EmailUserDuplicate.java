package com.victhorguilherme.finance_control.exceptions;

public class EmailUserDuplicate extends RuntimeException {
    public EmailUserDuplicate(String message) {
        super(message);
    }
}
