package me.dulce.gamesite.gamesite2.rooms.managers.games;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;

import java.util.UUID;

public class TestGame extends Room {
    public TestGame(UUID roomid, int maxUserCount, User host, SocketMessengerService messengerService) {
        super(roomid, maxUserCount, host, messengerService);
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
