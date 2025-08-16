package com.themall.accountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


// For 开发环境的快速调试 benefit， 将Spring Security从默认的'全部拦截'改为'全部放行'
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 禁用CSRF保护 + 全部拦截 改为-->全部放行'
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        
        return http.build();
    }
}