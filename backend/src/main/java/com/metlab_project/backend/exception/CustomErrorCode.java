package com.metlab_project.backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    FORBIDDEN_ACCESS_TO_OTHER_USER_INFO(HttpStatus.FORBIDDEN, "U002", "다른 사용자의 정보에 접근할 권한이 없습니다."),

    // 채팅룸 관련 에러
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "채팅방을 찾을 수 없습니다."),
    INSUFFICIENT_PARTICIPANTS(HttpStatus.BAD_REQUEST, "C002", "채팅방을 시작하기 위한 참가자 수가 부족합니다."),
    NOT_CHATROOM_HOST(HttpStatus.FORBIDDEN, "C003", "채팅방 호스트가 아닙니다."),
    CHATROOM_NOT_FULL(HttpStatus.BAD_REQUEST, "C004", "채팅방이 아직 가득 차지 않았습니다."),
    CHATROOM_FULL(HttpStatus.FORBIDDEN, "C005", "채팅방이 이미 가득 찼습니다."),
    NO_AUTHORITY_IN_CHATROOM(HttpStatus.FORBIDDEN, "C006", "채팅방에 대한 권한이 없습니다."),

    // 토큰 관련 에러
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T001", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "T002", "토큰이 유효하지 않습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "T003", "토큰이 누락되었습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "T004", "지원되지 않는 토큰입니다."),
    TOKEN_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED, "T005", "토큰 클레임이 비어있습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "T006", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T007", "리프레시 토큰이 만료되었습니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "T008", "블랙리스트에 등록된 토큰입니다."),

    // 그 외
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "내부 서버 오류가 발생했습니다.");

    @NonNull
    private final HttpStatus httpStatus;
    @NonNull
    private final String errorCode;
    @NonNull
    private final String errorMessage;
}