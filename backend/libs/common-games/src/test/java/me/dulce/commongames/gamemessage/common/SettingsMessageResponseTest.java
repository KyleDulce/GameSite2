package me.dulce.commongames.gamemessage.common;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameMessageTestParent;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import java.util.List;

public class SettingsMessageResponseTest extends GameMessageTestParent {
    @Override
    public GameMessage constructFromMessage(GameSerializableMessage message) {
        return new RoomMetaDataMessage(message);
    }

    @Override
    public GameMessage getTestGameDataInstance() {
        User user = User.createNewUser(sampleUUID, "name", "session");
        return new RoomMetaDataMessage(sampleUUID, List.of(user), "testGame", user);
    }
}
