package com.example.sparrow.configservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Extends {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 * so that can return {@link org.springframework.http.ProblemDetail} which satisfied RFC 9457.
 * <br></br>
 * Custom exception can extend {@link org.springframework.web.ErrorResponseException}
 * then spring can resolve without add handler in this class
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ProblemDetail handleThrowable(Throwable ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal server error");
        return problemDetail;
    }
}