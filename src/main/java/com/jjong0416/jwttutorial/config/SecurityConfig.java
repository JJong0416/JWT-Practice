package com.jjong0416.jwttutorial.config;

import com.jjong0416.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.jjong0416.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.jjong0416.jwttutorial.jwt.JwtSecurityConfig;
import com.jjong0416.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 기본적인 웹 보안을 활성화하겠다는 의미
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 어노테이션을 메소드 단위로 추가하기 위해서 미리 적요
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 생성자 주입
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                // exceptionHandling할 때, authenticationEntryPoint와 accessDeniedHandler를 우리가 만든걸로 추가해주기
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2 콘솔을 사용하기 위해 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()  // authorizeRequest는 HttpServletRequest를 사용하는 요청들에 대한 접그제한 설정
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                /* 토큰을 받기위한 로그인 api랑 회원 가입을 위한 api는 둘 다 토큰이 없는 상태에서 진행되기 때문에 열어준다.*/
                .anyRequest().authenticated()

                .and()
                // 그리고 마지막으로 JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스도 적응을 해준다.
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
