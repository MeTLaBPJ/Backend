package com.metlab_project.backend.domain.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAuthRequestDto {
    @Email
    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    @Schema(description = "이메일", example = "example@gmail.com")
    private String email;

    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    @Schema(description = "인증번호", example = "123456")
    private String authNum;
}