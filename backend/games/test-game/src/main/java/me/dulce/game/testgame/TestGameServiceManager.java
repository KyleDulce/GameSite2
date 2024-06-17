package me.dulce.game.testgame;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameServiceManager;
import me.dulce.commongames.gamemessage.GameMessageHandler;
import me.dulce.commongames.messaging.ClientMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestGameServiceManager implements GameServiceManager {
    public static String GAME_ID = "Test-TestGame";
    public static String GAME_DISPLAY = "Testing Game";

    @Autowired
    private TestGameMessageHandler testGameMessageHandler;

    @Override
    public String getGameId() {
        return GAME_ID;
    }

    @Override
    public String getGameDisplayName() {
        return GAME_DISPLAY;
    }

    @Override
    public Room createRoom(UUID roomId,
                           int maxUserCount,
                           User host,
                           String roomName,
                           ClientMessagingService messengerService) {
        return new TestGameRoom(roomId, maxUserCount, host, roomName, messengerService);
    }

    @Override
    public GameMessageHandler getGameMessageHandler() {
        return testGameMessageHandler;
    }
}
