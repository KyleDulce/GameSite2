package me.dulce.gamesite.gamesite2;

import com.fasterxml.jackson.databind.JsonNode;

public class GamesiteUtils {
    public static int getObjectIfDefinedOrDefault(JsonNode node, String field, int def) {
        if(node.has(field)) {
            return node.get(field).asInt();
        } else {
            return def;
        }
    }
    public static long getObjectIfDefinedOrDefault(JsonNode node, String field, long def) {
        if(node.has(field)) {
            return node.get(field).asLong();
        } else {
            return def;
        }
    }
    public static double getObjectIfDefinedOrDefault(JsonNode node, String field, double def) {
        if(node.has(field)) {
            return node.get(field).asDouble();
        } else {
            return def;
        }
    }
    public static boolean getObjectIfDefinedOrDefault(JsonNode node, String field, boolean def) {
        if(node.has(field)) {
            return node.get(field).asBoolean();
        } else {
            return def;
        }
    }
    public static String getObjectIfDefinedOrDefault(JsonNode node, String field, String def) {
        if(node.has(field)) {
            return node.get(field).asText();
        } else {
            return def;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(String str) {
        return isEmpty(str) || str.isBlank();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
