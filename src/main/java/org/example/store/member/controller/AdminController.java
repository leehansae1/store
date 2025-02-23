package org.example.store.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/member/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    // 예시: 미리 지정된 관리자 인증 정답 (실제 환경에서는 DB나 설정 파일에서 관리)
    private static final String ADMIN_SECRET = "adminSecret123";

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody Map<String, String> request) {
        String answer = request.get("answer");
        boolean verified = ADMIN_SECRET.equals(answer);
        return Map.of("verified", verified);
    }
}