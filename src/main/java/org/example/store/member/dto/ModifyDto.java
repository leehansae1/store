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
public class ModifyDto {
@NotBlank(message = "아이디는 필수 입력 사항입니다.")
  // @Size(min = 5, max = 20, message = "5글자 이상, 20글자 이하로 작성해주세요.")
  private String userId;

  // @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\\\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\\\":{}|<>]{8,20}$",
  // message = "비밀번호는 8자 이상, 20자 이하이며, 숫자와 특수문자를 포함해야 합니다.")
  private String userPw;

  private MultipartFile userProfile; // 파일을 MultipartFile로 받기

  @Email(message = "이메일 형식에 맞게 작성해주세요.")
  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  private String userEmail;

  @NotBlank(message = "이름은 필수 입력 사항입니다.")
  private String userName;

  private String address;

  private String tel;

  private String introduce;
}
