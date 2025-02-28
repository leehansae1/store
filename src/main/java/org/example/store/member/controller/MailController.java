package org.example.store.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.EmailDto;
import org.example.store.member.service.MailService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody EmailDto emailDto) {
        String targetEmail = emailDto.getEmail().replaceAll("^\"|\"$", "");
        log.info("Received email for confirmation: {}", targetEmail);
        String token = mailService.sendAuthMail(targetEmail);
        return Map.of("confirmNumber", token);
    }
}