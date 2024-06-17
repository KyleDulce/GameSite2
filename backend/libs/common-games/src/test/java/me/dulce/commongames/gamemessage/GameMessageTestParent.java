package me.dulce.commongames.gamemessage;

import static org.junit.jupiter.api.Assertions.*;

import me.dulce.commongames.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/** Since most GameData classes will have the same tests, this allows them to be flexible */
public abstract class GameMessageTestParent {

    public static final String sampleUuidString = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    public static final UUID sampleUUID = UUID.fromString(sampleUuidString);

    @Test
    public void testParseData_ParsesToGameDataAndBack_success() {
        GameMessage dataBefore = getTestGameDataInstance();
        GameSerializableMessage gameSerializableMessage = dataBefore.parseToSerializableObject();

        GameMessage dataAfter = constructFromMessage(gameSerializableMessage);
        assertPropertiesMatch(dataBefore, dataAfter);
    }

    @AfterEach
    public void afterEach() {
        User.clearCache();
    }

    public abstract GameMessage constructFromMessage(GameSerializableMessage message);

    public abstract GameMessage getTestGameDataInstance();

    public void assertPropertiesMatch(GameMessage before, GameMessage after) {
        assertEquals(after, before);
    }
}
