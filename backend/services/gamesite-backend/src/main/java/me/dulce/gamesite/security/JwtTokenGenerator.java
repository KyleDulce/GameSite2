package me.dulce.gamesite.security;

import me.dulce.commongames.User;
import me.dulce.gamesite.configuration.AppConfig;
import me.dulce.gamesite.user.UserSessionManager;
import me.dulce.gamesite.utilservice.TimeService;
import me.dulce.gamesite.utilservice.UserSecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class JwtTokenGenerator {
    public static final String SESSION_ID_CLAIM = "sessionId";

    @Autowired private TimeService timeService;
    @Autowired private AppConfig appConfig;
    @Autowired private JwtEncoder jwtEncoder;
    @Autowired private UserSessionManager userSessionManager;

    public String generateJwtToken(Authentication authentication) {
        User user = UserSecurityUtils.getUserSecurityDetailsFromPrincipal(authentication);
        Instant now = timeService.getCurrentInstant();
        String sessionId = userSessionManager.generateNewSession(user);

        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" "));

        JwtClaimsSet claimsSet =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(
                                now.plus(
                                        appConfig.getUserActivityTimeoutSeconds(),
                                        ChronoUnit.SECONDS))
                        .subject(authentication.getName())
                        .claim("scope", authorities)
                        .claim(SESSION_ID_CLAIM, sessionId)
                        .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
