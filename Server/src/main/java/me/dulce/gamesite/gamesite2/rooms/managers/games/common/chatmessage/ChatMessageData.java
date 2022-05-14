package me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataType;

import java.util.UUID;

public class ChatMessageData extends GameData {

    public UUID roomId;
    public String messageText;
    public String senderName;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public GameDataType gameDataType() {
        return GameDataType.CHAT_MESSAGE;
    }

    @Override
    protected void setupFromGameDataMessage(GameDataMessage message) throws Exception {
        ChatMessage parsedMessage = new ObjectMapper().convertValue(message.data, ChatMessage.class);
        roomId = UUID.fromString(message.roomId);
        messageText = parsedMessage.message;
        senderName = parsedMessage.senderName;
    }

    @Override
    protected Object onGetParse() throws Exception {

        return null;
    }

    private static class ChatMessage{
        String message;
        String senderName;
    }
}
