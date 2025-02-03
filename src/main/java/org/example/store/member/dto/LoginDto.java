package org.example.store.member.dto;

import jakarta.validation.constraints.NotBlank;
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
public class LoginDto {

  @NotBlank(message = "아이디를 입력하세요.")
  private String userId;

  @NotBlank(message = "패스워드를 입력하세요.")
  private String userPw;
}
