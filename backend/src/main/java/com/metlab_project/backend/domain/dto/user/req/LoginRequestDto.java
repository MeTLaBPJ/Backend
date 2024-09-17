package com.metlab_project.backend.domain.dto.user.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String schoolEmail;
    private String password;
}