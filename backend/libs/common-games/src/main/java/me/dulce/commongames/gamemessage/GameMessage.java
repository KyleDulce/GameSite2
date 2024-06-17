package me.dulce.commongames.gamemessage;

import java.io.Serializable;
import java.util.UUID;

public abstract class GameMessage {
    /**
     * returns the id of the room
     *
     * @return the id of the current room
     */
    public abstract UUID roomId();

    public abstract String getDataIdString();

    /**
     * Converts message to an object that can be serialized and sent
     *
     * @return Serializable objct
     */
    public abstract Serializable onParseData();

    public GameSerializableMessage parseToSerializableObject() {
        return new GameSerializableMessage(getDataIdString(), roomId().toString(), onParseData());
    }
}
