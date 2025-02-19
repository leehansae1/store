package org.example.store.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;

  // 랜덤 숫자 생성 메서드
  private String makeRandomNumber() {
    Random random = new Random();
    int randomNumber = 100000 + random.nextInt(900000);
    return Integer.toString(randomNumber);
  }

  public String sendAuthMail(String userEmail) {
    MimeMessage message = javaMailSender.createMimeMessage();
    try {
      message.setFrom("test9876test@naver.com"); // 보내는 사람
      message.setRecipients(MimeMessage.RecipientType.TO, userEmail);
      message.setSubject("WAVE Market 이메일 인증");

      String token = makeRandomNumber();

      // 인증 링크 생성 (예시 URL, 실제 도메인으로 변경)
      String content = "<h1 style='text-align:center'>"+token+"</h1>"
              + "<h3 style='text-align:center'>인증번호를 복사하여 이메일 인증을 완료해주세요.</h3>";
      message.setContent(content, "text/html; charset=UTF-8");
      javaMailSender.send(message);
      return token;
    } catch (MessagingException e) {
      throw new RuntimeException("메일 전송 실패", e);
    }
  }
}
