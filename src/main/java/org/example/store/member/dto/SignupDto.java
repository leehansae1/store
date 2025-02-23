package org.example.store.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignupDto {

    @NotBlank(message = "아이디는 필수 입력 사항입니다.")
    private String userId;

    @NotBlank(message = "패스워드는 필수 입력 사항입니다.")
    private String userPw;

    private MultipartFile userProfile;

    @Email(message = "이메일 형식에 맞게 작성해주세요.")
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    private String userEmail;

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String userName;

    private String address;
    private String tel;
    private String introduce;

    // 관리자 여부 전달 (ROLE_USER 기본)
    private String role;
}
