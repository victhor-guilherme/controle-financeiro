package com.victhorguilherme.finance_control.exceptions;

public class ResourceNotFound extends RuntimeException{

    public ResourceNotFound(String mensagem){
        super(mensagem);
    }
}
