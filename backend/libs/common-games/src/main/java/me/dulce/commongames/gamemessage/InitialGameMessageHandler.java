package me.dulce.commongames.gamemessage;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameResolver;
import me.dulce.commongames.game.GameServiceManager;
import me.dulce.commongames.gamemessage.common.ChatMessageMessage;
import me.dulce.commongames.gamemessage.common.CommonGameDataType;
import me.dulce.commongames.gamemessage.common.KickPlayerMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InitialGameMessageHandler implements GameMessageHandler {

    @Autowired private GameResolver gameResolver;

    @Override
    public boolean handleGameMessageObject(
            User sender, GameSerializableMessage gameSerializableMessage, Room room) {
        return CommonGameDataType.getTypeFromId(gameSerializableMessage.gameDataIdString)
                .map(
                        commonGameDataType ->
                                switch (commonGameDataType) {
                                    case CHAT_MESSAGE ->
                                            room.processChatMessage(
                                                    new ChatMessageMessage(gameSerializableMessage),
                                                    sender);
                                    case SETTINGS_DATA_REQUEST ->
                                            room.processSettingsDataRequest(sender);
                                    case KICK_PLAYER ->
                                            room.processPlayerKickRequest(
                                                    sender,
                                                    new KickPlayerMessage(gameSerializableMessage));
                                    default -> false;
                                })
                .orElseGet(() -> handleAlternateGameHandler(sender, gameSerializableMessage, room));
    }

    private boolean handleAlternateGameHandler(
            User sender, GameSerializableMessage gameSerializableMessage, Room room) {
        Optional<GameServiceManager> gameServiceManagerOptional =
                gameResolver.getGameServiceManagerFromId(room.getGameId());
        if (gameServiceManagerOptional.isEmpty()) {
            throw new IllegalStateException("gameId without game service manager");
        }

        return gameServiceManagerOptional
                .get()
                .getGameMessageHandler()
                .handleGameMessageObject(sender, gameSerializableMessage, room);
    }
}
