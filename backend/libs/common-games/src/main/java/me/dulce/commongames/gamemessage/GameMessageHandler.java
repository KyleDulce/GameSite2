package me.dulce.commongames.gamemessage;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;

public interface GameMessageHandler {
    boolean handleGameMessageObject(
            User sender, GameSerializableMessage gameSerializableMessage, Room room);
}
