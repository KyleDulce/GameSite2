package me.dulce.gamesite.gamesite2.utilservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * General Utility class for common methods
 */
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
     * @param str the string to check
     * @return true if the string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if the String is null, empty or contains only whitespace
     * @param str the string to check
     * @return true if the string is null, empty or blank
     */
    public static boolean isBlank(String str) {
        return isEmpty(str) || str.isBlank();
    }

    /**
     * Checks if the String is not null nor empty
     * @param str the string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if the string is not null, empty nor containing only whitespace
     * @param str the string to check
     * @return true if the string is not null empty, nor blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Checks whether a given instant is within a given age based on now
     * @param instant the instant to check
     * @param maxAgeSeconds the max age allowed
     * @param now current instant
     * @return true if it within the age range, false otherwise
     */
    public static boolean isInstantWithinAge(Instant instant, long maxAgeSeconds, Instant now) {
        return now.isBefore(instant.plus(maxAgeSeconds, ChronoUnit.SECONDS));
    }

    public static String generateRandomSessionId() {
        return UUID.randomUUID().toString();
    }
}
