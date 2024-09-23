package com.metlab_project.backend.domain.dto.user.res;

import lombok.*;

@Data
@Getter@Setter
@Builder
@NoArgsConstructor
public class UserUpdateInfoResponse {
    private String schoolEmail;
    private boolean ok;

    public UserUpdateInfoResponse(String schoolEmail, boolean b) {
        this.schoolEmail = schoolEmail;
        this.ok = b;
    }
}