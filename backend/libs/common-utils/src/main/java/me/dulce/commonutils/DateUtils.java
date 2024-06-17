package me.dulce.commonutils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DateUtils {
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
}
