package me.dulce.gamesite.gamesite2;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * General Utility class for common methods
 */
public class GamesiteUtils {
    /**
     * Tries to get int from JSON node of a specified field, returns default if not available
     * @param node The JSON node to read
     * @param field The JSON Field to read
     * @param def Default value if field is not available
     * @return the resulting int
     */
    public static int getObjectIfDefinedOrDefault(JsonNode node, String field, int def) {
        if(node.has(field)) {
            return node.get(field).asInt();
        } else {
            return def;
        }
    }

    /**
     * Tries to get long from JSON node of a specified field, returns default if not available
     * @param node The JSON node to read
     * @param field The JSON Field to read
     * @param def Default value if field is not available
     * @return the resulting long
     */
    public static long getObjectIfDefinedOrDefault(JsonNode node, String field, long def) {
        if(node.has(field)) {
            return node.get(field).asLong();
        } else {
            return def;
        }
    }

    /**
     * Tries to get double from JSON node of a specified field, returns default if not available
     * @param node The JSON node to read
     * @param field The JSON Field to read
     * @param def Default value if field is not available
     * @return the resulting double
     */
    public static double getObjectIfDefinedOrDefault(JsonNode node, String field, double def) {
        if(node.has(field)) {
            return node.get(field).asDouble();
        } else {
            return def;
        }
    }

    /**
     * Tries to get boolean from JSON node of a specified field, returns default if not available
     * @param node The JSON node to read
     * @param field The JSON Field to read
     * @param def Default value if field is not available
     * @return the resulting boolean
     */
    public static boolean getObjectIfDefinedOrDefault(JsonNode node, String field, boolean def) {
        if(node.has(field)) {
            return node.get(field).asBoolean();
        } else {
            return def;
        }
    }

    /**
     * Tries to get String from JSON node of a specified field, returns default if not available
     * @param node The JSON node to read
     * @param field The JSON Field to read
     * @param def Default value if field is not available
     * @return the resulting String
     */
    public static String getObjectIfDefinedOrDefault(JsonNode node, String field, String def) {
        if(node.has(field)) {
            return node.get(field).asText();
        } else {
            return def;
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
}
