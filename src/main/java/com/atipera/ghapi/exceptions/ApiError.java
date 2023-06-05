package com.atipera.ghapi.exceptions;



import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public
class ApiError {

    private HttpStatus status;


    private String Message;


    private ApiError() {

    }


    ApiError(HttpStatus status) {
        this.status = status;
    }


    public ApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.Message = message;
    }
}
