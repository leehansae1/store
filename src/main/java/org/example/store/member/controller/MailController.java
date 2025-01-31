package org.example.store.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.example.store.member.service.MailService;


@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

  private final MailService mailService;

  private String prefix = "/mail";

  @GetMapping("/mail-auth")
  public String mailAuth() {
    return prefix + "/mail-auth";
  }
  @PostMapping("/mail-auth")
  public String sendMail(@RequestParam("userEmail") String userEmail) {
    mailService.sendAuthMail(userEmail);
    return prefix + "/mail-auth";
  }
}
