package br.com.eventos.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import java.util.List;

// JwtAuthFilter.java (exemplo)
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) { this.jwt = jwt; }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        // 1) se não tem Authorization, não bloqueia: deixa seguir
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 2) se tem, tenta validar
        String token = auth.substring(7);
        try {
            if (jwt.validate(token)) {
                var email = jwt.getSubject(token);
                // coloque o principal como e-mail (ou um UserDetails se você usa)
                var authToken = new UsernamePasswordAuthenticationToken(email, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // mesmo com token inválido, deixe seguir; as rotas protegidas serão barradas depois
            chain.doFilter(request, response);
        } catch (Exception e) {
            // opcional: não devolva 403 aqui para não quebrar endpoints públicos
            chain.doFilter(request, response);
        }
    }
}