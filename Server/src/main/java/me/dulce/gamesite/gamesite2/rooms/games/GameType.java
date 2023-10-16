package me.dulce.gamesite.gamesite2.rooms.games;

import java.lang.reflect.Constructor;
import java.util.UUID;

import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestGame;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.user.User;

/**
 * Types of games
 */
public enum GameType {
    /**
     * Null Game
     */
    NULL_GAME_TYPE(-1, "NULL", null),
    /**
     * Game Type for testing purposes
     */
    TEST(-2, "Test", TestGame.class)
    ;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameType.class);
    private final int id;
    private final String name;
    private final Class<? extends Room> attachedClass;
    GameType(int id, String name, Class<? extends Room> attachedClass) {
        this.id = id;
        this.name = name;
        this.attachedClass = attachedClass;
    }

    public String toString() {
        return name;
    }

    public Room createRoomInstance(UUID uuid, User host, int maxPlayers, String roomName, SocketMessengerService messengerService) {
        try {
            Constructor<? extends Room> objectConstructor = attachedClass.getConstructor(UUID.class, Integer.TYPE, User.class, String.class, SocketMessengerService.class);
            return objectConstructor.newInstance(uuid, maxPlayers, host, roomName, messengerService);
        } catch(Exception e) {
            LOGGER.error("Failed to create Room Instance of type {}, Exception: {}", name, e.getMessage());
            return null;
        }
    }

    public int getId() {
        return id;
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
