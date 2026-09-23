package com.example.guessgame.config;

import com.example.guessgame.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.guessgame.model.User;
import com.example.guessgame.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

String token = null;

if (cookies != null) {
    for (Cookie cookie : cookies) {
        if ("token".equals(cookie.getName())) {
            token = cookie.getValue();
            break;
        }
    }
}

if (token != null && !token.isBlank()) {

    String username = jwtService.extractUsername(token);

    if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null &&
            jwtService.validateToken(token, username)) {

        Optional<User> user =
                userRepository.findByUsername(username);

        if (user.isPresent()) {

            String role =
                    "ROLE_" + user.get().getRole().name();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
    }
}

        filterChain.doFilter(request, response);
    }
}