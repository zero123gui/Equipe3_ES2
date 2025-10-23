package br.com.eventos.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(cs -> cs.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/register-full").permitAll()

                        .requestMatchers(HttpMethod.GET, "/events/**", "/talks/**").permitAll()

                        // eventos: precisam estar logados (o service barra quem não for admin)
                        .requestMatchers(HttpMethod.POST,   "/events/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/events/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/events/**").authenticated()

                        // (opcional, melhor) proteger change-password
                        .requestMatchers(HttpMethod.POST, "/auth/change-password").authenticated()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // evite abrir tudo de /auth/**
                        //.requestMatchers("/auth/**").permitAll()

                        .anyRequest().authenticated()
                )



                // JWT na frente do filtro padrão de auth
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
