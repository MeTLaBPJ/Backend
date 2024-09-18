package com.metlab_project.backend.domain.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailPostRequestDto {
    @Email
    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    @Schema(description = "받는 사람 이메일 주소", example = "example@gmail.com")
    private String email;
}