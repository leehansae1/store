package org.example.store.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

import org.example.store.member.dto.EmailDto;
import org.example.store.member.service.MailService;


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
