package com.metlab_project.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP 400 상태 코드 반환
public class EmailAuthNotEqualsException extends RuntimeException {
    private final CustomErrorCode errorCode;

    public EmailAuthNotEqualsException() {
        super("Email authentication code does not match."); // 기본 메시지
        this.errorCode = CustomErrorCode.UNKNOWN; // 에러 코드 설정
    }

    public CustomErrorCode getErrorCode() {
        return errorCode;
    }
}
