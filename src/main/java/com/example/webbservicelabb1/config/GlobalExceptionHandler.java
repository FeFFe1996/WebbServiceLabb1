package com.example.webbservicelabb1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleTooManyRequests(RuntimeException e, Model model){
        logger.warn("Error to many requests " + e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions(Exception e){
        logger.warn("Error: " + e.getMessage());
        return "redirect:/";
    }

}
