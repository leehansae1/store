package org.example.store.member.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails originalDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = originalDetails.getLoggedMember();

        // ✅ 비밀번호 제거된 복사본 생성
        Member safeCopy = Member.builder()
                .userId(member.getUserId())
                .userPw("")  // 비번 제거
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

        // ✅ 복사된 CustomUserDetails로 다시 생성
        CustomUserDetails safeDetails = new CustomUserDetails(safeCopy);

        // ✅ 세션 교체 (Spring Security Context도 덮어씌움)
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT",
                new SecurityContextImpl(authentication)); // 기존 authentication 재사용 가능
        request.getSession().setAttribute("loggedUser", safeDetails); // 필요 시 사용자 저장

        response.sendRedirect("/");
    }
}
