package com.fastcampus.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorAware(){
        return ()-> Optional.of("seungJi"); // TODO: 스프링시큐리티로 인증기능을 추가할 때 고정적으로 붙이는 seungji를 수정하자.
    }

}
