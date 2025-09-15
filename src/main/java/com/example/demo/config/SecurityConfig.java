package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    // DefaultLoginPageGenerationFilter 에서 제공하는 기본 로그인 화면 사용
    // 로그인 기능이 없어서 다른 기능 확인 불가하므로 로그인 안해도 작업 가능하게 설정 작업

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll())
                .formLogin(withDefaults());

        return http.build();
    }
}
