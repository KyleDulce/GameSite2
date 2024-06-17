package me.dulce.game.testgame;

import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import java.io.Serializable;
import java.util.UUID;

/** Basic Template for Message data objects */
public class TestMessage extends GameMessage {
    public static String MESSAGE_ID = "Test-TestMessage";

    public UUID roomId;
    public String message;

    public TestMessage(GameSerializableMessage message) {
        roomId = UUID.fromString(message.roomId);
        this.message = (String) message.data;
    }

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public String getDataIdString() {
        return MESSAGE_ID;
    }

    @Override
    public Serializable onParseData() {
        return message;
    }
}
