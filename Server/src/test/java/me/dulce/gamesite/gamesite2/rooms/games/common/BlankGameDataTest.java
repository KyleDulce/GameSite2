package me.dulce.gamesite.gamesite2.rooms.games.common;

import static org.junit.jupiter.api.Assertions.*;

import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;

public class BlankGameDataTest extends GameDataTestParent {
    @Override
    public GameData getTestGameDataInstance() {
        return new BlankGameData(sampleUUID, GameDataType.TEST);
    }

    @Override
    public GameData getBlankGameDataInstance() {
        return new BlankGameData();
    }

    @Override
    public void assertPropertiesMatch(GameData before, GameData after) {
        BlankGameData beforeData = (BlankGameData) before;
        BlankGameData afterData = (BlankGameData) after;
        assertEquals(beforeData.roomId, afterData.roomId);
        assertEquals(beforeData.gameDataType, afterData.gameDataType);
    }
}
