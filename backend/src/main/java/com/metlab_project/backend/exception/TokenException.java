package com.metlab_project.backend.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends RuntimeException {

    private final TokenErrorCode errorCode;
    private final HttpStatus httpStatus;

    public TokenException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public TokenException(TokenErrorCode errorCode, String details) {
        super(errorCode.getMessage() + ": " + details);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public TokenErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public enum TokenErrorCode {
        TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token has expired"),
        TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid"),
        TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Token is missing"),
        TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "Token is unsupported"),
        TOKEN_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED, "Token claims are empty"),
        REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh token not found"),
        REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token has expired"),
        TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "Token has been blacklisted");

        private final HttpStatus httpStatus;
        private final String message;

        TokenErrorCode(HttpStatus httpStatus, String message) {
            this.httpStatus = httpStatus;
            this.message = message;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }
    }
}
