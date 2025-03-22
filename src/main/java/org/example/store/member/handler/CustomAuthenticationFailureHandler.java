package org.example.store.member.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.error("🔥 로그인 실패: {}", exception.getMessage());
        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호를 확인해주세요."; // ✅ 비밀번호 오류
        } else if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            log.info("cause {}", cause.getMessage());
            if (cause instanceof BadCredentialsException && "존재하지 않는 아이디입니다.".equals(cause.getMessage())) {
                errorMessage = "존재하지 않는 아이디입니다.";
            } else if (cause instanceof DisabledException) {
                errorMessage = "이 계정은 탈퇴 처리되었습니다. 새로운 계정을 생성하세요.";
            } else errorMessage = "아이디 또는 비밀번호를 확인해주세요.";
        } else errorMessage = "로그인 오류. 관리자에게 문의 부탁드립니다.";

        response.sendRedirect("/member/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}

