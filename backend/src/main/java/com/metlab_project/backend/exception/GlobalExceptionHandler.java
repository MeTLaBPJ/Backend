package com.metlab_project.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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