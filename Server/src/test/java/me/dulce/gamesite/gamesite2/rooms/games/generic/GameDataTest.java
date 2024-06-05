package me.dulce.gamesite.gamesite2.rooms.games.generic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestMessageData;
import org.junit.jupiter.api.Test;

public class GameDataTest {
    private static final String ROOM_UUID = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";

    @Test
    public void testParseObjectToDataMessage_success() {
        String expectedDataString = "Test";
        GameData gameDataObj =
                new GameData() {
                    @Override
                    public UUID roomId() {
                        return UUID.fromString(ROOM_UUID);
                    }

                    @Override
                    public GameDataType gameDataType() {
                        return GameDataType.TEST;
                    }

                    @Override
                    public void setupFromGameDataMessage(GameDataMessage message) throws Exception {
                        // do nothing
                    }

                    @Override
                    public Object onGetParse() throws Exception {
                        return expectedDataString;
                    }
                };

        GameDataMessage actual = gameDataObj.parseObjectToDataMessage();

        assertEquals(ROOM_UUID, actual.roomId);
        assertEquals(GameDataType.TEST.toString(), actual.gameDataIdString);
        assertEquals(expectedDataString, actual.data);
    }

    @Test
    public void testParseObjectToDataMessage_exceptionThrow_noValueObject() {
        GameData gameDataObj =
                new GameData() {
                    @Override
                    public UUID roomId() {
                        return UUID.fromString(ROOM_UUID);
                    }

                    @Override
                    public GameDataType gameDataType() {
                        return GameDataType.TEST;
                    }

                    @Override
                    public void setupFromGameDataMessage(GameDataMessage message) throws Exception {
                        // do nothing
                    }

                    @Override
                    public Object onGetParse() throws Exception {
                        throw new Exception("Fake Exception");
                    }
                };

        GameDataMessage actual = gameDataObj.parseObjectToDataMessage();

        assertEquals(ROOM_UUID, actual.roomId);
        assertEquals(GameDataType.TEST.toString(), actual.gameDataIdString);
        assertEquals(GameData.NO_VALUE_STRING, actual.data);
    }

    @Test
    public void testGetGameDataFromMessage_success() {
        String expectedStringData = "Test";
        GameDataMessage testGameData =
                GameDataMessage.builder()
                        .data(expectedStringData)
                        .roomId(ROOM_UUID)
                        .gameDataIdString(GameDataType.TEST.toString())
                        .build();

        Optional<GameData> actual = GameData.getGameDataFromMessage(testGameData);

        assertTrue(actual.isPresent());
        assertEquals(GameDataType.TEST, actual.get().gameDataType());
        assertEquals(ROOM_UUID, actual.get().roomId().toString());
        assertEquals(expectedStringData, ((TestMessageData) actual.get()).message);
    }

    @Test
    public void testGetGameDataFromMessage_nullMessage_EmptyOptional() {
        Optional<GameData> actual = GameData.getGameDataFromMessage(null);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetGameDataFromMessage_nullType_EmptyOptional() {
        GameDataMessage testGameData = new GameDataMessage();

        Optional<GameData> actual = GameData.getGameDataFromMessage(testGameData);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetGameDataFromMessage_nullGameType_EmptyOptional() {
        GameDataMessage testGameData = new GameDataMessage();
        testGameData.gameDataIdString = GameDataType.NULL.toString();

        Optional<GameData> actual = GameData.getGameDataFromMessage(testGameData);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetGameDataFromMessage_inValidType_EmptyOptional() {
        GameDataMessage testGameData = new GameDataMessage();
        testGameData.gameDataIdString = "Invalid Game Type";

        Optional<GameData> actual = GameData.getGameDataFromMessage(testGameData);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetGameDataFromMessage_ExceptionThrownOnParse_EmptyOptional() {
        GameDataMessage testGameData = new GameDataMessage();
        testGameData.gameDataIdString = "Invalid Game Type";
        // will throw a cast exception as String cannot be cast from object
        testGameData.data = new Object();

        Optional<GameData> actual = GameData.getGameDataFromMessage(testGameData);

        assertTrue(actual.isEmpty());
    }
}
