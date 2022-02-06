package me.dulce.gamesite.gamesite2.rooms.managers.games;

import java.lang.reflect.Constructor;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.user.User;

public enum GameType {
        
    ;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameType.class);
    private int id;
    private String name;
    private Class<? extends Room> attatchedClass;
    private GameType(int id, String name, Class<? extends Room> attatchedClass) {
        this.id = id;
        this.name = name;
        this.attatchedClass = attatchedClass;
    }

    public String toString() {
        return name;
    }

    public Room createRoomInstance(UUID uuid, User host, int maxPlayers) {
        try {
            Constructor<? extends Room> objectConstructor = attatchedClass.getConstructor(UUID.class, Integer.TYPE, User.class);
            Room roomInstance = objectConstructor.newInstance(uuid, maxPlayers, host);
            return roomInstance;
        } catch(Exception e) {
            LOGGER.error("Failed to create Room Instance of type {}, Exception: {}", name, e.getMessage());
            return null;
        }
    }

    public static GameType getGameTypeFromString(String name) {
        GameType[] values = GameType.class.getEnumConstants();
        for(GameType gameType : values) {
            if(gameType.name.equalsIgnoreCase(name)) {
                return gameType;
            }
        }
        return null;
    }

    public static GameType getGameTypeFromId(int id) {
        GameType[] values = GameType.class.getEnumConstants();
        for(GameType gameType : values) {
            if(gameType.id == id) {
                return gameType;
            }
        }
        return null;
    }
}
