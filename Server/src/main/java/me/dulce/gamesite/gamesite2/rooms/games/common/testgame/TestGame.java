package me.dulce.gamesite.gamesite2.rooms.games.common.testgame;

import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;

import java.util.UUID;

/**
 * Gametype for testing purposes
 */
public class TestGame extends Room {
    public TestGame(UUID roomId, int maxUserCount, User host, String roomName, SocketMessengerService messengerService) {
        super(roomId, maxUserCount, host, roomName, messengerService);
    }

    @Override
    public GameType getGameType() {
        return GameType.TEST;
    }

    @Override
    protected boolean processGameDataForGame(User user, GameData response) {
        return true;
    }
    @Override
    protected void onUserJoinEvent(User user) {}
    @Override
    protected void onSpectatorJoinEvent(User user) {}
    @Override
    protected void onUserLeaveEvent(User user) {}
}
