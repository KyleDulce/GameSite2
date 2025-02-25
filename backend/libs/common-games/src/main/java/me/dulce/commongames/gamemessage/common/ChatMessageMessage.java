package me.dulce.commongames.gamemessage.common;

import lombok.*;

import me.dulce.commongames.gamemessage.IncomingGameData;
import me.dulce.commongames.gamemessage.OutgoingGameData;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@IncomingGameData("ChatMessageData")
public class ChatMessageMessage implements OutgoingGameData {
    private String messageText;
    private String senderName;

    @Override
    public String getDataIdString() {
        return "ChatMessageData";
    }
}
