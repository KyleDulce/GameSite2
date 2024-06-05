package me.dulce.gamesite.gamesite2.rooms.games.common.settings;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import me.dulce.gamesite.gamesite2.rooms.games.common.GameDataTestParent;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.AfterAll;

public class KickPlayerDataTest extends GameDataTestParent {
    private User user =
            User.createNewUser(
                    UUID.fromString("eb0f39e0-d108-4bc9-83cd-1e12d4b0c784"),
                    "someName",
                    "someSession");

    @AfterAll
    public static void afterTests() {
        User.clearCache();
    }

    @Override
    public GameData getTestGameDataInstance() {
        User.addUserToCache(user);
        return new KickPlayerData(sampleUUID, user);
    }

    @Override
    public GameData getBlankGameDataInstance() {
        return new KickPlayerData();
    }

    @Override
    public void assertPropertiesMatch(GameData before, GameData after) {
        KickPlayerData beforeData = (KickPlayerData) before;
        KickPlayerData afterData = (KickPlayerData) after;
        assertEquals(beforeData.roomId, afterData.roomId);
        assertEquals(beforeData.player, afterData.player);
    }
}
