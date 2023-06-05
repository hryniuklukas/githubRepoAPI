package com.atipera.ghapi.exceptions;

public class WrongHeaderException extends Exception{
    public WrongHeaderException(String errorMessage){
        super(errorMessage);
    }
}
