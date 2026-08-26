package com.cryptotracker.backend.infrastructure.security;

import jakarta.persistence.Column;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String autHeader = request.getHeader("Authorization");

        if (autHeader != null && autHeader.startsWith("Bearer ")) {
            String token = autHeader.substring(7);

            if (jwtTokenProvider.isTokenValid(token)) {
                String email = jwtTokenProvider.extractEmail(token);
                String role = jwtTokenProvider.extractRole(token);
                // role null ise (eski token) bos liste — kullanici authenticate olur ama rol olmaz
                List<GrantedAuthority> authorities = (role != null && !role.isBlank())
                        ? List.of(new SimpleGrantedAuthority(role))
                        : List.of();

                // SecurityContext'e set et — Spring artık bu kullanıcıyı tanır
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        filterChain.doFilter(request, response);  // bir sonraki filtreye geç

    }
}
