package me.dulce.commongames.gamemessage.common;

import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameMessageTestParent;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

public class ChatMessageMessageTest extends GameMessageTestParent {
    @Override
    public GameMessage constructFromMessage(GameSerializableMessage message) {
        return new ChatMessageMessage(message);
    }

    @Override
    public GameMessage getTestGameDataInstance() {
        return new ChatMessageMessage(sampleUUID, "some text", "user send");
    }
}
