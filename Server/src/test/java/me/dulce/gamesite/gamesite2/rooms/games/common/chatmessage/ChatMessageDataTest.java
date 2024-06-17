package me.dulce.gamesite.gamesite2.rooms.games.common.chatmessage;

import static org.junit.jupiter.api.Assertions.*;

import me.dulce.gamesite.gamesite2.rooms.games.common.GameDataTestParent;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;

public class ChatMessageDataTest extends GameDataTestParent {

    @Override
    public GameData getTestGameDataInstance() {
        return new ChatMessageData(sampleUUID, "Test Message Text", "RandomSender");
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
