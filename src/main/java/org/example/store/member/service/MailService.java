package org.example.store.member.service;

import java.util.Random;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private final MemberService memberService;

  // private String sender = "dragont1ger@naver.com";

  private String makeRandomNumber() {
    Random random = new Random();
    int randomNumber = 100000 + random.nextInt(900000);
    return Integer.toString(randomNumber);
  }

  public void sendAuthMail(String userEmail) {
    MimeMessage message = javaMailSender.createMimeMessage();
      try {
        message.setFrom("dragont1ger@naver.com");
        message.setRecipients(MimeMessage.RecipientType.TO, userEmail);
        message.setSubject("이메일 인증 번호");
        String sendRandomNumber = makeRandomNumber();
        message.setText("<h1 style='text-align:center'>"+sendRandomNumber+"</h1>","UTF-8","html");
        javaMailSender.send(message);
      } catch (MessagingException e) {
        throw new RuntimeException("메일 전송 실패", e);
      }
  }
}
