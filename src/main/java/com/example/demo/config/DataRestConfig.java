package com.example.demo.config;

import com.example.demo.domain.UserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class DataRestConfig {
    // Data Rest: Rest Controller를 자동으로 생성해 주는 편리한 모듈
    // 자동 생성 기능 중에 읽기, 삭제가 안 되는 것을 원할 때도 있는데, 3가지 방법으로 구현
    // RestConfig에서 설정하는 방법, 스프링 Security를 적용하는 방법,  Data Rest의 자동 생성을 사용하지 않고 그냥 수동으로 개발하는 방법

    @Bean
    public RepositoryRestConfigurer repositoryRestConfig() {
        return RepositoryRestConfigurer.withConfig((config, cors) ->
                config.exposeIdsFor(UserAccount.class));
    }
}
