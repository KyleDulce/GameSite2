package me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage;

import me.dulce.gamesite.gamesite2.rooms.managers.games.common.GameDataTestParent;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageDataTest extends GameDataTestParent {

    @Override
    public GameData getTestGameDataInstance() {
        return new ChatMessageData(UUID.fromString(sampleUuidString), "Test Message Text", "RandomSender");
    }

    @Override
    public GameData getBlankGameDataInstance() {
        return new ChatMessageData();
    }

    @Override
    public void assertPropertiesMatch(GameData before, GameData after) {
        ChatMessageData beforeChat = (ChatMessageData) before;
        ChatMessageData afterChat = (ChatMessageData) after;
        assertEquals(beforeChat.roomId, afterChat.roomId);
        assertEquals(beforeChat.messageText, afterChat.messageText);
        assertEquals(beforeChat.senderName, afterChat.senderName);
    }
}
