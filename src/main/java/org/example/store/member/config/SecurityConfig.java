package org.example.store.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.handler.CustomAuthenticationFailureHandler;
import org.example.store.member.handler.CustomLoginSuccessHandler;
import org.example.store.member.handler.CustomOAuth2SuccessHandler;
import org.example.store.member.service.OAuth2DetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final OAuth2DetailService oAuth2DetailService;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // 🔥 CSRF 비활성화 (POST 요청 문제 해결)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index", "/css/**", "/js/**", "/img/**", "/video/**",
                                "/product/list/**", "/product/list", "/upload/**", "/product/detail/**",
                                "/member/login", "/member/signup", "/member/admin/verify", "/member/check-duplicate",
                                "/mail/**", "/shop/**", "/external/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(auth -> auth
                        .loginPage("/member/login")
                        .usernameParameter("userId")
                        .passwordParameter("userPw")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/", true) // 🔥 이전 페이지로 리디렉트
                        .failureHandler(customAuthenticationFailureHandler)
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                .logout(auth -> auth
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/") // 🔥 로그아웃 후 메인 페이지로 이동
                        .invalidateHttpSession(true)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2DetailService)
                        )
                        .failureHandler((request, response, exception) -> {
                            log.error("🔥 소셜 로그인 실패! 이유={}", exception.getMessage(), exception);
                            request.getRequestDispatcher("/member/login").forward(request, response);
                        })
                        .successHandler(customOAuth2SuccessHandler)
                );
        return httpSecurity.build();
    }
}
