package com.example.sparrow.configservice.exception;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Illegal state");
        logException(ex);
        return problemDetail;
    }

    @ExceptionHandler(Throwable.class)
    public ProblemDetail handleThrowable(Throwable ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal server error");
        logException(ex);
        return problemDetail;
    }

    private void logException(Throwable ex) {
        log.error("Something went wrong with throwable ", ex);
    }
}