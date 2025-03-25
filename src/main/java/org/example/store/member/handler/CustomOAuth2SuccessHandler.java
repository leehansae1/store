package org.example.store.member.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails originalDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = originalDetails.getLoggedMember();
        Map<String, Object> attributes = originalDetails.getAttributes();

        // 비밀번호 제거된 복사본 생성
        Member safeCopy = Member.builder()
                .userId(member.getUserId())
                .userPw("")  // 비밀번호 제거
                .userName(member.getUserName())
                .userEmail(member.getUserEmail())
                .userProfile(member.getUserProfile())
                .address(member.getAddress())
                .tel(member.getTel())
                .introduce(member.getIntroduce())
                .role(member.getRole())
                .regDate(member.getRegDate())
                .status(member.getStatus())
                .randomId(member.getRandomId())
                .build();

        // 복사된 CustomUserDetails 생성 (attributes 유지)
        CustomUserDetails safeDetails = new CustomUserDetails(safeCopy, attributes);

        // 세션 교체
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT",
                new SecurityContextImpl(authentication));
        request.getSession().setAttribute("loggedUser", safeDetails);

        response.sendRedirect("/");
    }
}
