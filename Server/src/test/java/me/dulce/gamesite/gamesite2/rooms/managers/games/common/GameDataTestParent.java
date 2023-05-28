package me.dulce.gamesite.gamesite2.rooms.managers.games.common;

import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import org.junit.jupiter.api.Test;

/**
 * Since most GameData classes will have the same tests, this allows them to be flexible
 */
public abstract class GameDataTestParent {

    public static final String sampleUuidString = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";

    @Test
    public void testParseData_ParsesToGameDataAndBack_success() throws Exception {
        GameData dataBefore = getTestGameDataInstance();
        Object obj = dataBefore.onGetParse();
        GameDataMessage message = GameDataMessage.builder()
                .gameDataIdString(dataBefore.gameDataType().toString())
                .roomId(dataBefore.roomId().toString())
                .data(obj)
                .build();

        GameData dataAfter = getBlankGameDataInstance();
        dataAfter.setupFromGameDataMessage(message);

        assertPropertiesMatch(dataBefore, dataAfter);
    }

    public abstract GameData getTestGameDataInstance();
    public abstract GameData getBlankGameDataInstance();
    public abstract void assertPropertiesMatch(GameData before, GameData after);
}
