package me.dulce.gamesite.gamesite2.rooms.games.common.settings;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.rooms.games.common.GameDataTestParent;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.AfterAll;

public class SettingsDataResponseTest extends GameDataTestParent {
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
        return new SettingsDataResponse(sampleUUID, List.of(user));
    }

    @Override
    public GameData getBlankGameDataInstance() {
        return new SettingsDataResponse();
    }

    @Override
    public void assertPropertiesMatch(GameData before, GameData after) {
        SettingsDataResponse beforeData = (SettingsDataResponse) before;
        SettingsDataResponse afterData = (SettingsDataResponse) after;
        assertEquals(beforeData.roomId, afterData.roomId);
        assertEquals(1, beforeData.players.size());
        assertEquals(beforeData.players, afterData.players);
    }
}
