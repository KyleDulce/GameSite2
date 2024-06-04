package me.dulce.gamesite.gamesite2.utilservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.security.UserSecurityDetails;
import me.dulce.gamesite.gamesite2.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

/** General Utility class for common methods */
public class GamesiteUtils {

  public static Optional<UUID> getUUIDFromString(String uuidStr) {
    try {
      return Optional.of(UUID.fromString(uuidStr));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Checks if the String is null or empty
   *
   * @param str the string to check
   * @return true if the string is null or empty
   */
  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * Checks if the String is null, empty or contains only whitespace
   *
   * @param str the string to check
   * @return true if the string is null, empty or blank
   */
  public static boolean isBlank(String str) {
    return isEmpty(str) || str.isBlank();
  }

  /**
   * Checks if the String is not null nor empty
   *
   * @param str the string to check
   * @return true if the string is not null and not empty
   */
  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  /**
   * Checks if the string is not null, empty nor containing only whitespace
   *
   * @param str the string to check
   * @return true if the string is not null empty, nor blank
   */
  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  /**
   * Checks whether a given instant is within a given age based on now
   *
   * @param instant the instant to check
   * @param maxAgeSeconds the max age allowed
   * @param now current instant
   * @return true if it within the age range, false otherwise
   */
  public static boolean isInstantWithinAge(Instant instant, long maxAgeSeconds, Instant now) {
    return now.isBefore(instant.plus(maxAgeSeconds, ChronoUnit.SECONDS));
  }

  public static User getUserSecurityDetailsFromPrincipal(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof Jwt) {
      return User.getUserFromUUID(((Jwt) principal).getSubject())
          .orElseThrow(() -> new JwtException("Invalid JWT"));
    } else if (principal instanceof UserSecurityDetails) {
      return User.getUserFromSecurityDetails((UserSecurityDetails) principal);
    } else {
      throw new IllegalStateException(
          "Authentication Principal is neither JWT nor security details");
    }
  }
}
