package org.example.store.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Map<String, Object> confirm(@RequestBody String email) {
        log.info("email == {}", email);
        email = email.replaceAll("^\"|\"$", "");
        log.info("changedEmail == {}", email);
        String token = mailService.sendAuthMail(email);
        return Map.of("confirmNumber", token);
    }
}
