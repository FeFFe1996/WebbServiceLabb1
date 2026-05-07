package com.example.webbservicelabb1.config;

import com.example.webbservicelabb1.exception.RateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleTooManyRequests(RateLimitExceededException e){
        logger.warn("Error: rate limit exception " + e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests, please try again later");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e){
        logger.warn("Internal server error: " + e.getMessage());
        if (e instanceof ResponseStatusException rsE){
            return ResponseEntity.status(rsE.getStatusCode()).body(rsE.getReason());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred on the server.");
    }

}
