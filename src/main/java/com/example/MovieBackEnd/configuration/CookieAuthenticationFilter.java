package com.example.MovieBackEnd.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieAuthenticationFilter extends OncePerRequestFilter {
    public static final String COOKIE_NAME = "accessToken";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(httpServletRequest.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
        if (cookieAuth.isPresent()) {
            String accessToken= cookieAuth.get().getValue();
            System.out.println(accessToken);
            PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken(accessToken, true);
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
