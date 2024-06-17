package me.dulce.game.testgame;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.messaging.ClientMessagingService;

import java.util.UUID;

import static me.dulce.game.testgame.TestGameServiceManager.GAME_ID;

/** Gametype for testing purposes */
public class TestGameRoom extends Room {

    public TestGameRoom(UUID roomId, int maxUserCount, User host, String roomName, ClientMessagingService messengerService) {
        super(roomId, maxUserCount, host, roomName, messengerService);
    }

    @Override
    public String getGameId() {
        return GAME_ID;
    }

    @Override
    public void onUserJoinEvent(User user) {}

    @Override
    public void onSpectatorJoinEvent(User user) {}

    @Override
    public void onUserLeaveEvent(User user) {}
}
