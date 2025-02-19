package org.example.store.member.config;

import lombok.RequiredArgsConstructor;
import org.example.store.member.service.OAuth2DetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/static/**", "/index", "/css/**", "/js/**", "/img/**", "/video/**",
                                "/product/list/**", "/product/list", "/upload/**", "/product/detail/**",
                                "/member/**", "/mail/**",  "/external/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(auth -> auth
                        .loginPage("/member/login") // 로그인 페이지 URL
                        .usernameParameter("userId")
                        .passwordParameter("userPw")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/member/login?error=true")
                        .permitAll()
                )
                .logout(auth -> auth
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                // OAuth2 소셜 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/index", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2DetailService))
                );

        return httpSecurity.build();
    }

    // CORS 설정 빈 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 외부 요청 허용 (필요시 특정 도메인만 허용하도록 변경 가능)
        configuration.setAllowedOrigins(List.of(
                "https://t1.kakaocdn.net/online_payment/online-payment-pc-bridge-client/3c296bcc363f6110d94150d63869bd3dcb442001/static/index-78371517.css",
                "https://online-payment.kakaopay.com",
                "https://t1.kakaocdn.net/online_payment/online-payment-pc-bridge-client/3c296bcc363f6110d94150d63869bd3dcb442001/static/index-78371517.css"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // 쿠키 전송 등 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
