package main.music.config;

import jakarta.servlet.http.HttpServletResponse;
import main.music.security.UserAuthenticationFilter;
import main.music.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/register").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().authenticated())
                .csrf(csrf->csrf.disable())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 정책 설정
//                        .invalidSessionUrl("/login") // 세션이 만료된 경우 로그인 페이지로 리다이렉트
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL 설정
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                // 인증된 사용자가 로그아웃 요청을 할 때 세션을 무효화
                                new SecurityContextLogoutHandler().logout(request, response, authentication);

                                // 세션 쿠키 삭제 (JSESSIONID 초기화)
                                response.setHeader("Set-Cookie", "JSESSIONID=; Max-Age=0; Path=/;");

                                // 로그아웃 후 200 OK 응답 반환
                                response.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                // 인증되지 않은 상태에서 로그아웃 요청이 들어왔을 때도 200 OK 반환
                                response.setStatus(HttpServletResponse.SC_OK);
                            }
                        })
                )

                .addFilterBefore(new UserAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 리액트 서버 URL
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정 적용
        return source;}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
