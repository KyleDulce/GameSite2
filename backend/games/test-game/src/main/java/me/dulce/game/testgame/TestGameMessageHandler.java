package me.dulce.game.testgame;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessageHandler;
import me.dulce.commongames.gamemessage.GameSerializableMessage;
import org.springframework.stereotype.Service;

@Service
public class TestGameMessageHandler implements GameMessageHandler {
    @Override
    public boolean handleGameMessageObject(User sender, GameSerializableMessage gameSerializableMessage, Room room) {
        if(!gameSerializableMessage.gameDataIdString.equals(TestMessage.MESSAGE_ID)) {
            return false;
        }
        return true;
    }
}
