package org.example.store.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginDto {

  @NotBlank(message = "아이디를 입력하세요.")
  private String userId;

  @NotBlank(message = "패스워드를 입력하세요.")
  private String userPw;
}
