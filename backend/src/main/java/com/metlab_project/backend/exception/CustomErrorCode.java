package com.metlab_project.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    // 채팅룸 관련 에러
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "Chat room not found"),
    INSUFFICIENT_PARTICIPANTS(HttpStatus.BAD_REQUEST, "C002", "Insufficient participants to start chat room"),
    NOT_CHATROOM_HOST(HttpStatus.FORBIDDEN, "C003", "User is not the host of the chat room"),
    // 토큰 관련 에러
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401", "Token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "401", "Token is invalid"),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "401", "Token is missing"),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "401", "Token is unsupported"),
    TOKEN_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED, "401", "Token claims are empty"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401", "Refresh token has expired"),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "401", "Token has been blacklisted");

    @NonNull
    private final HttpStatus httpStatus;
    @NonNull
    private final String errorCode;
    @NonNull
    private final String errorMessage;
}