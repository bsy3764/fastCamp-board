package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing  // jpa auditing 사용
@Configuration
public class JpaConfig {

    // 날짜는 현재의 시간을 넣으면 되지만 @CreatedBy, @LastModifiedBy 의 값을 가져오기 위해 작성
    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정 필요
        return () -> Optional.of("uno");
    }
}
