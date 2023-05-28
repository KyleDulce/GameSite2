package me.dulce.gamesite.gamesite2.rooms.managers.games.common.testgame;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
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
}
