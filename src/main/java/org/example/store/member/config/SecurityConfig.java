package org.example.store.member.config;

import lombok.RequiredArgsConstructor;
import org.example.store.member.service.OAuth2DetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2DetailService oAuth2DetailService;

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new ForwardAuthenticationFailureHandler("/member/login"); // 실패 시 로그인 페이지로 리다이렉트
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                        (auth) -> auth // 허용된 패턴들은 인증 없이 접근 가능 나머지 모든 요청은 인증을 요구
                                .requestMatchers("/", "/index", "/css/**", "/js/**", "/img/**", "/video/**",
                                        "/product/list/**", "/product/list", "/upload/**", "/product/detail/**",
                                        "/member/**", "/mail/**") // 허용된 URL 패턴들
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(
                        (auth) -> auth
                                .loginPage("/member/login") // 로그인 페이지의 URL 지정
                                .usernameParameter("userId") // 로그인 폼에서 사용자 아이디를 "userId" 파라미터로 처리
                                .passwordParameter("userPw")
                                .loginProcessingUrl("/member/login") // POST 요청이 여기로 보내져야 인증 진행
                                .defaultSuccessUrl("/", true) // 로그인 성공 시 리다이렉트할 기본 URL
                                .failureUrl("/member/login?error=true") // 로그인 실패 시 리다이렉트
                                .permitAll() // 로그인 페이지는 인증 없이 접근 가능
                )
                .logout(
                        (auth) -> auth
                                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 요청을 받을 URL
                                .logoutSuccessUrl("/") // 로그아웃 성공 후 다이렉트할 URL
                                .invalidateHttpSession(true) // 로그아웃 후 세션을 무효시켜 로그아웃
                )
                .csrf((csrf) -> csrf.disable());  // 배포 시에는 주석처리하고 위 괄호에서 ;으로 마무리하기

        // OAuth2 소셜 로그인 설정
        httpSecurity.oauth2Login(
                (auth) -> auth
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/index", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2DetailService))
        );
        return httpSecurity.build();
    }
}
