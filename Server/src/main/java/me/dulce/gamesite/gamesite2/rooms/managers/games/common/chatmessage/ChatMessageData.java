package me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataType;

import java.util.UUID;

/**
 * Chat Message Data for chat messages incoming or outgoing
 */
@AllArgsConstructor
@NoArgsConstructor
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
    public void setupFromGameDataMessage(GameDataMessage message) throws Exception {
        ChatMessage parsedMessage = new ObjectMapper().convertValue(message.data, ChatMessage.class);
        roomId = UUID.fromString(message.roomId);
        messageText = parsedMessage.message;
        senderName = parsedMessage.senderName;
    }

    @Override
    public Object onGetParse() throws Exception {
        ChatMessage result = new ChatMessage();
        result.message = messageText;
        result.senderName = senderName;
        return result;
    }

    public static class ChatMessage{
        public String message;
        public String senderName;
    }
}
