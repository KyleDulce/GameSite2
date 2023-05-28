package me.dulce.gamesite.gamesite2.rooms.managers.games.common.joingame;

import static org.junit.jupiter.api.Assertions.*;

import me.dulce.gamesite.gamesite2.rooms.managers.games.common.GameDataTestParent;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;

import java.util.UUID;

public class JoinRoomGameDataTest extends GameDataTestParent {
    @Override
    public GameData getTestGameDataInstance() {
        return new JoinRoomGameData(UUID.fromString(sampleUuidString));
    }

    @Override
    public GameData getBlankGameDataInstance() {
        return new JoinRoomGameData();
    }

    @Override
    public void assertPropertiesMatch(GameData before, GameData after) {
        JoinRoomGameData beforeChat = (JoinRoomGameData) before;
        JoinRoomGameData afterChat = (JoinRoomGameData) after;
        assertEquals(beforeChat.roomId, afterChat.roomId);
    }
}
