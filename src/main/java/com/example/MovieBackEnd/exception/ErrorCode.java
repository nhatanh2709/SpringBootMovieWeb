package com.example.MovieBackEnd.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error" ,HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Invalid message key",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User existed",HttpStatus.BAD_REQUEST),
    EMAIL_EXITED(1003, "Email existed",HttpStatus.BAD_REQUEST),
    EMAIL_NULL(1004, "Email not null",HttpStatus.BAD_REQUEST),
    PASSWORD_NULL(1005,"Password not null",HttpStatus.BAD_REQUEST),
    USERNAME_NULL(1006,"Username not null",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1007,"Username must be at least 3 characters",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1008,"Password must be at least 6 characters",HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1009, "Email not existed",HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1010,"Password not correct",HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1011,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1012,"you do not have a permission", HttpStatus.FORBIDDEN)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;




}
