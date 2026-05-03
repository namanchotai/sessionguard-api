package com.sessionguard.security;

import com.sessionguard.entity.Session;
import com.sessionguard.exception.TokenInvalidException;
import com.sessionguard.repository.BlacklistedTokenRepository;
import com.sessionguard.repository.SessionRepository;
import com.sessionguard.service.CustomUserDetailsService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistRepo;
    private final SessionRepository sessionRepo;

    @Resource(name = "handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            if (blacklistRepo.existsByToken(token)) {
                throw new TokenInvalidException("Token revoked");
            }

            String email = jwtUtil.extractEmail(token);
            Long sessionId = jwtUtil.extractSessionId(token);

            if (email == null || sessionId == null) {
                throw new TokenInvalidException("Invalid token");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Session session = sessionRepo.findById(sessionId).orElse(null);

                if (session == null || !session.isActive()) {
                    throw new TokenInvalidException("Session expired");
                }

                if (!jwtUtil.validateToken(token)) {
                    throw new TokenInvalidException("Invalid token");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }
}