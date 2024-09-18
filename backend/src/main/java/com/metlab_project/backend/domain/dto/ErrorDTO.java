package com.metlab_project.backend.domain.dto;

import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorDTO {

    private final String code;
    private final String message;
    private final String detail;

    public static ResponseEntity<ErrorDTO> of(CustomException ex) {
        CustomErrorCode errorCode = ex.getCustomErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorDTO.builder()
                        .code(errorCode.getErrorCode())
                        .message(errorCode.getErrorMessage())
                        .detail(ex.getDetail())
                        .build());
    }
}