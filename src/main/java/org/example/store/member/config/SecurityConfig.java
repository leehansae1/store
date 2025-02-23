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
        // 인증 실패 시 /member/login으로 포워딩
        return new ForwardAuthenticationFailureHandler("/member/login");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/static/**", "/index", "/css/**", "/js/**", "/img/**", "/video/**",
                                "/product/list/**", "/product/list", "/upload/**", "/product/detail/**",
                                "/member/**", "/mail/**", "/external/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(auth -> auth
                        .loginPage("/member/login")
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
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/index", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2DetailService))
                );
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
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
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
