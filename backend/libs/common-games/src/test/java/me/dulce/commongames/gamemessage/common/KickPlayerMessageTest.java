package me.dulce.commongames.gamemessage.common;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameMessageTestParent;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

public class KickPlayerMessageTest extends GameMessageTestParent {
    @Override
    public GameMessage constructFromMessage(GameSerializableMessage message) {
        return new KickPlayerMessage(message);
    }

    @Override
    public GameMessage getTestGameDataInstance() {
        return new KickPlayerMessage(sampleUUID, User.createNewUser(sampleUUID, "name", "session"));
    }
}
