package com.metlab_project.backend.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomErrorCode customErrorCode;
    private final String detail;

    public CustomException(CustomErrorCode customErrorCode) {
        this.customErrorCode = customErrorCode;
        this.detail = "";
    }

    public CustomException(CustomErrorCode errorCode, String detail) {
        this.customErrorCode = errorCode;
        this.detail = detail;
    }

    public CustomException(Exception exception) {
        if (exception instanceof CustomException) {
            CustomException customException = (CustomException) exception;
            this.customErrorCode = customException.getCustomErrorCode();
            this.detail = customException.getDetail();
        } else {
            this.customErrorCode = CustomErrorCode.UNKNOWN;
            this.detail = exception.getMessage();
        }
    }
}

