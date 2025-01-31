package com.dragontiger.prjectwave.member.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignupDto {

  @NotBlank(message = "아이디는 필수 입력 사항입니다.")
  // @Size(min = 5, max = 20, message = "5글자 이상, 20글자 이하로 작성해주세요.")
  private String userId;

  // @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\\\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\\\":{}|<>]{8,20}$",
  // message = "비밀번호는 8자 이상, 20자 이하이며, 숫자와 특수문자를 포함해야 합니다.")
  private String userPw;

  private MultipartFile userProfile;  // 프로필 이미지 파일

  @Email(message = "이메일 형식에 맞게 작성해주세요.")
  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  private String userEmail;

  @NotBlank(message = "이름은 필수 입력 사항입니다.")
  private String userName;

  private String addr01;

  private String addr02;

  private String zipcode;

  private String tel;

  private String introduce;
  

}
