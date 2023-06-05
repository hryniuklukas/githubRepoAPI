package com.atipera.ghapi.exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
