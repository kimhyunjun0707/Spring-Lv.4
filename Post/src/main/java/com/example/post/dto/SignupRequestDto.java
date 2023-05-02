package com.example.post.dto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    @NotEmpty
    @Size(min = 4, max = 10,message = "아이디는4~10자로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]*$",message = "아이디는 알파벳 소문자(a~z), 숫자(0~9)에서 입력해주세요")
    private String username;
    @NotBlank
    @NotEmpty
    @Size(min = 8, max = 15 ,message = "비밀번호는 8자이상 15자이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]+$",message = "비밀번호는 알파벳 소문자(a~z), 대문자(A~Z), 숫자(0~9) 범위내에서 입력해주세요.")
    private String password;

    private boolean admin = false;
    private String adminToken = "";

}
