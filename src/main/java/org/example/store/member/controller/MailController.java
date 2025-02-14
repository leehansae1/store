package org.example.store.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.store.member.dto.EmailDto;
import org.example.store.member.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

  private final MailService mailService;

  @PostMapping("/confirm")
  @ResponseBody
  public Map<String, String> confirm(@RequestBody EmailDto emailDto) {
    String randomNumber = mailService.sendAuthMail(emailDto.getEmail());
    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("confirmNumber", randomNumber);
    return resultMap;
  }
}
