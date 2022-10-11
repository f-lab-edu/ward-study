package com.dsg.wardstudy.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Range(min = 2, max = 12, message = "이름은 2글자 이상 12글자 이하여야 합니다.")
    private String name;
    @Email
    private String email;

    @NotBlank
    @Range(min = 2, max = 16, message = "이름은 2글자 이상 16글자 이하여야 합니다.")
    private String nickname;

    @NotBlank
    private String password;

    @Builder
    public SignUpRequest(String name, String email, String nickname, String password) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
