package me.dulce.commongames.gamemessage.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;

import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ChatMessageMessage extends GameMessage {
    private UUID roomId;
    private String messageText;
    private String senderName;

    public ChatMessageMessage(GameSerializableMessage gameSerializableMessage) {
        roomId = UUID.fromString(gameSerializableMessage.roomId);
        ChatMessage parsedMessage =
                new ObjectMapper().convertValue(gameSerializableMessage.data, ChatMessage.class);
        messageText = parsedMessage.message;
        senderName = parsedMessage.senderName;
    }

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public String getDataIdString() {
        return "common-chat-message";
    }

    @Override
    public Serializable onParseData() {
        return new ChatMessage(messageText, senderName);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage implements Serializable {
        public String message;
        public String senderName;
    }
}
