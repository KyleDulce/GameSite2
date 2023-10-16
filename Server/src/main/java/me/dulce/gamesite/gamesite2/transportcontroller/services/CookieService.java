package me.dulce.gamesite.gamesite2.transportcontroller.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.user.User;
import me.dulce.gamesite.gamesite2.utilservice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.Instant;

/**
 * Token Service that handles Cookies
 */
@Service
public class CookieService {

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.ES256;
    private static final String UUID_CLAIM = "uuid";
    private static final String SESSION_CLAIM = "session";
    private static final String COOKIE_CLAIM = "tokenId";
    private static final String NAME_CLAIM = "name";
    public static final String AUTH_COOKIE_ID = "Game-AuthCookie";

    private final AppConfig config;
    private final TimeService timeService;
    private final RoomManager roomManager;
    private final KeyPair jwtKey;
    private Random randomObj;

    @Autowired
    public CookieService(AppConfig config, TimeService timeService, RoomManager roomManager) {
        this.config = config;
        this.timeService = timeService;
        this.roomManager = roomManager;
        randomObj = new Random();
        //generate new key on each startup
        this.jwtKey = Keys.keyPairFor(SIGNATURE_ALGORITHM);
    }

    @Scheduled(fixedDelayString = "${auth.cacheClearIntervalSeconds}")
    public void clearCachedUsersTask() {
        Collection<User> users = User.getCachedUsers().values();
        Instant now = timeService.getCurrentInstant();
        for(User user : users) {
            if(user.getLastActiveTime() == null
                    || !GamesiteUtils.isInstantWithinAge(user.getLastActiveTime(),
                        config.getUserActivityTimeoutSeconds(), now)) {
                User.getCachedUsers().remove(user.getUuid());
                //kick user from rooms
                UUID roomId = roomManager.getRoomThatContainsUser(user);
                roomManager.processUserLeaveRoomRequest(user, roomId);
            }
        }
        randomObj = new Random();
    }

    /**
     * Gets new user jwt token for session
     * @param user
     * @return
     */
    public String generateNewUserJwtToken(User user) {
        Instant currentTime = timeService.getCurrentInstant();
        long cookieValue = randomObj.nextLong();
        user.getCookieIds().add(cookieValue);
        return Jwts.builder()
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(currentTime.plus(config.getUserActivityTimeoutSeconds(), ChronoUnit.SECONDS)))
                .claim(UUID_CLAIM, user.getUuid())
                .claim(SESSION_CLAIM, user.getSessionId())
                .claim(COOKIE_CLAIM, cookieValue)
                .claim(NAME_CLAIM, user.getName())
                .signWith(jwtKey.getPrivate(), SIGNATURE_ALGORITHM)
                .compact();
    }

    /**
     * Gets new user cookie
     * @param user
     */
    public ResponseCookie getUserCookie(User user) {
        String jws = generateNewUserJwtToken(user);

        return ResponseCookie.from(AUTH_COOKIE_ID, jws)
                .maxAge(config.getUserActivityTimeoutSeconds())
                .httpOnly(false)
                .sameSite(Cookie.SameSite.LAX.attributeValue())
                .path("/")
                .build();
    }

    /**
     * validates cookie from user. Will update lastActive variable in user if validation successful
     * @param cookieString The cookie jwt to validate
     * @return Optional with user if validated, empty otherwise
     */
    public Optional<User> validateUserCookie(String cookieString) {
        try {
            Instant now = timeService.getCurrentInstant();
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(jwtKey.getPublic())
                    .build()
                    .parseClaimsJws(cookieString);

            Instant expirationDate = claims.getBody().getExpiration().toInstant();
            if(!GamesiteUtils.isInstantWithinAge(expirationDate, config.getUserActivityTimeoutSeconds(), now)) {
                return Optional.empty();
            }
            String uuid = claims.getBody().get(UUID_CLAIM, String.class);
            String sessionId = claims.getBody().get(SESSION_CLAIM, String.class);
            long cookieValue = claims.getBody().get(COOKIE_CLAIM, Long.class);

            //verify uuid and session
            Optional<User> user = User.getUserFromUUID(uuid);
            if(user.isEmpty()) {
                return Optional.empty();
            }

            if(!user.get().getSessionId().equals(sessionId)) {
                return Optional.empty();
            }

            if(!user.get().getCookieIds().contains(cookieValue)) {
                return Optional.empty();
            }

            user.get().updateActiveTime(now);
            return user;
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getNameFromCookie(String cookieString) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(jwtKey.getPublic())
                    .build()
                    .parseClaimsJws(cookieString);

            String name = claims.getBody().get(NAME_CLAIM, String.class);
            return Optional.of(name);
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    /**
     * Invalidates user session and returns cookie for deleting
     * @param user
     * @return
     */
    public ResponseCookie getDeleteCookie(User user) {
        User.getCachedUsers().remove(user.getUuid());
        user.setSessionId(null);
        user.getCookieIds().clear();
        //kick user from rooms
        UUID roomId = roomManager.getRoomThatContainsUser(user);
        roomManager.processUserLeaveRoomRequest(user, roomId);
        return ResponseCookie.from(AUTH_COOKIE_ID, null)
                .path("/")
                .sameSite(Cookie.SameSite.LAX.attributeValue())
                .build();
    }
}
