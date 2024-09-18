package com.metlab_project.backend.domain.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailMessageDto {
    @Email
    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    private String to;

    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    private String subject;

    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    private String message;
}