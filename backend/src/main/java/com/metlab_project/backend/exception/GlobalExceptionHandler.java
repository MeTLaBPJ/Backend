package com.metlab_project.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        logger.error("CustomException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(ex.getCustomErrorCode().getHttpStatus())
                .body(new CustomErrorResponse(ex.getCustomErrorCode(), ex.getDetail()));
    }
}