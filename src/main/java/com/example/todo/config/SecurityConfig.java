package com.example.todo.config;

import com.example.todo.config.auth.OAuth2SuccessHandler;
import com.example.todo.config.auth.OAuth2UserServiceImpl;
import com.example.todo.config.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2UserServiceImpl oAuth2UserServiceImpl;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(authHttp -> authHttp
                        .requestMatchers("/token/**", "/views/**")
                        .permitAll()
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .permitAll())

                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/views/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserServiceImpl))
                )

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(
                        jwtFilter,
                        AuthorizationFilter.class);

        return http.build();
    }
    @Bean
    // 비밀번호 암호화를 위한 Bean
    public BCryptPasswordEncoder passwordEncoder(){
        // 기본적으로 사용자 비밀번호는 해독가능한 형태로 데이터베이스에
        // 저장되면 안된다. 그래서 기본적으로 비밀번호를 단방향 암호화 하는
        // 인코더를 사용한다.
        return new BCryptPasswordEncoder();
    }
}
