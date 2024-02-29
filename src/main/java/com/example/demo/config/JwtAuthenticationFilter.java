package com.example.demo.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("cookie") + ";";
        //final String authHeader = request.getHeader("Authorization"); //for postman
        final String jwt;
        final String userEmail;
        if (!authHeader.contains("jwtToken=") ) {
            filterChain.doFilter(request, response);
            return;
        }
        int tokenIndex = authHeader.indexOf("jwtToken=") + 9;
        jwt = authHeader.substring(tokenIndex, authHeader.indexOf(";",tokenIndex));
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(jwt)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, null, Stream.of(jwtService.extractRole(jwt))
                                                            .map(SimpleGrantedAuthority::new)
                                                            .collect(Collectors.toList())
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            else {
                Cookie cookie = new Cookie("jwtToken", "");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                response.sendRedirect("/login?showWarning=true");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
