package me.dulce.commongames.gamemessage;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class InitialGameMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameResolver.class);

    @Autowired private GameResolver gameResolver;
    @Autowired private GameMessageResolver gameMessageResolver;

    public void handleGameMessageObject(
            User sender, GameSerializableMessage gameSerializableMessage, Room room) {

        Serializable messageData = gameSerializableMessage.data;
        Optional<Class<? extends Serializable>> gameMessageClass =
                gameMessageResolver.resolveGameMessage(gameSerializableMessage);
        if (gameMessageClass.isEmpty()) {
            // It is a blank message. It is handled by a string handler
            gameMessageClass = Optional.of(String.class);
            messageData = gameSerializableMessage.gameDataIdString;
        }

        String gameId = room.getGameId();
        List<Method> eventListeners =
                gameResolver.getEventListenerMethodFromGameIdAndGameMessageType(
                        gameId, gameMessageClass.get());
        for (Method eventListener : eventListeners) {
            try {
                eventListener.invoke(room, sender, messageData);
            } catch (Exception e) {
                LOGGER.error("Error invoking Game Message Listener", e);
            }
        }
    }
}
