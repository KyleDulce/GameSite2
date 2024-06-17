package me.dulce.gamesite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import me.dulce.commongames.User;
import me.dulce.gamesite.configuration.AppConfig;
import me.dulce.gamesite.user.UserSessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtSecurityCookieService {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtSecurityCookieService.class);
    private final String COOKIE_NAME = "Game-AuthCookie";
    private final String COOKIE_BASE_PATH = "/api";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserSessionManager userSessionManager;
    private final AppConfig appConfig;

    @Autowired
    public JwtSecurityCookieService(
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            JwtTokenGenerator jwtTokenGenerator,
            UserSessionManager userSessionManager,
            AppConfig appConfig) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userSessionManager = userSessionManager;
        this.appConfig = appConfig;
    }

    public OncePerRequestFilter getCookieFilter() {
        return new JwtSecurityCookieFilter();
    }

    public ResponseCookie generateNewResponseCookie(Authentication authentication) {
        String token = jwtTokenGenerator.generateJwtToken(authentication);

        return ResponseCookie.from(COOKIE_NAME, token)
                .maxAge(appConfig.getUserActivityTimeoutSeconds())
                .httpOnly(false)
                .path(COOKIE_BASE_PATH)
                .build();
    }

    public ResponseCookie getDeleteCookie(User user) {
        return ResponseCookie.from(COOKIE_NAME, null).path(COOKIE_BASE_PATH).build();
    }

    private void doCookieFilter(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtStr = extractJwtFromCookies(request);
        if (jwtStr != null) {
            try {
                Jwt token = jwtDecoder.decode(jwtStr);
                String sessionId = token.getClaimAsString("sessionId");
                if (!userSessionManager.isValidSession(sessionId)) {
                    throw new JwtException("Invalid Session!");
                }

                AbstractAuthenticationToken authToken = jwtAuthenticationConverter.convert(token);

                LOGGER.info("DECODED {}", authToken);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(COOKIE_NAME)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private class JwtSecurityCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            doCookieFilter(request, response, filterChain);
        }
    }
}
