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

  // 인증 메일을 보내는 메서드
  public String sendAuthMail(String userEmail) {
    MimeMessage message = javaMailSender.createMimeMessage();
      try {
        message.setFrom("dragont1ger@naver.com"); // 보내는 사람
        message.setRecipients(MimeMessage.RecipientType.TO, userEmail); // 받는 사람
        message.setSubject("이메일 인증 번호");
        String sendRandomNumber = makeRandomNumber();
        message.setText("<h1 style='text-align:center'>"+sendRandomNumber+"</h1>","UTF-8","html");
        javaMailSender.send(message);
        return sendRandomNumber;
      } catch (MessagingException e) {
        throw new RuntimeException("메일 전송 실패", e);
      }
  }
}
