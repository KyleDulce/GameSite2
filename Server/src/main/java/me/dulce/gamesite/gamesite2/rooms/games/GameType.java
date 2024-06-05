package me.dulce.gamesite.gamesite2.rooms.games;

import java.lang.reflect.Constructor;
import java.util.UUID;
import lombok.Getter;
import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestGame;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Types of games */
public enum GameType {
    /** Null Game */
    NULL_GAME_TYPE("NULL", "NULL", null),
    /** Game Type for testing purposes */
    TEST("TESTING", "Test", TestGame.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(GameType.class);
    @Getter private final String id;
    private final String name;
    private final Class<? extends Room> attachedClass;

    GameType(String id, String name, Class<? extends Room> attachedClass) {
        this.id = id;
        this.name = name;
        this.attachedClass = attachedClass;
    }

    public String toString() {
        return name;
    }

    public Room createRoomInstance(
            UUID uuid,
            User host,
            int maxPlayers,
            String roomName,
            SocketMessengerService messengerService) {
        try {
            Constructor<? extends Room> objectConstructor =
                    attachedClass.getConstructor(
                            UUID.class,
                            Integer.TYPE,
                            User.class,
                            String.class,
                            SocketMessengerService.class);
            return objectConstructor.newInstance(
                    uuid, maxPlayers, host, roomName, messengerService);
        } catch (Exception e) {
            LOGGER.error(
                    "Failed to create Room Instance of type {}, Exception: {}",
                    name,
                    e.getMessage());
            return null;
        }
    }

    public static GameType getGameTypeFromString(String name) {
        GameType[] values = GameType.class.getEnumConstants();
        for (GameType gameType : values) {
            if (gameType.name.equalsIgnoreCase(name)) {
                return gameType;
            }
        }
        return null;
    }

    public static GameType getGameTypeFromId(String id) {
        GameType[] values = GameType.class.getEnumConstants();
        for (GameType gameType : values) {
            if (gameType.id.equals(id)) {
                return gameType;
            }
        }
        return null;
    }
}
