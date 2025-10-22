package com.griddynamics.gridu.pbazhko.tests.config;

import com.griddynamics.gridu.pbazhko.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> handleGenericException(Exception ex) {
        return Mono.just(Map.of("error", "Unexpected error occurred"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return Mono.just(Map.of("error", ex.getMessage()));
    }
}
