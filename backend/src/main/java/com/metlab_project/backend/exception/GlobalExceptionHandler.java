package com.metlab_project.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        log.error("CustomException occurred: {}", ex.getMessage(), ex.getDetail(), ex.getCustomErrorCode(), ex.getCause());
        return ResponseEntity
                .status(ex.getCustomErrorCode().getHttpStatus())
                .body(new CustomErrorResponse(ex.getCustomErrorCode(), ex.getDetail()));
    }
}