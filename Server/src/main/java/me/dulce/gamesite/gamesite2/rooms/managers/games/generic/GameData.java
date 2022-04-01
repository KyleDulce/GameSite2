package me.dulce.gamesite.gamesite2.rooms.managers.games.generic;

import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public abstract class GameData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameType.class);

    public abstract UUID roomId();
    public abstract GameDataType gameDataType();

    protected abstract void setupFromGameDataMessage(GameDataMessage message) throws Exception;
    protected abstract Object onGetParse() throws Exception;

    public GameDataMessage parseObjectToDataMessage() {
        GameDataMessage gameDataMessage = new GameDataMessage();
        gameDataMessage.roomId = roomId().toString();
        gameDataMessage.gameDataIdString = gameDataType().toString();
        try {
            gameDataMessage.data = onGetParse();
        } catch (Exception e) {
            LOGGER.error("Error on parsing object of type {}", gameDataType().toString(), e);
            gameDataMessage.data = "No Value!";
        }
        return gameDataMessage;
    }

    public static Optional<GameData> getGameDataFromMessage(GameDataMessage message) {
        if(message == null) {
            return Optional.empty();
        }
        GameDataType type = GameDataType.getGameDataTypeFromString(message.gameDataIdString);
        if(type == null || type == GameDataType.NULL) {
            return Optional.empty();
        }
        Optional<GameData> createdObjectOptional = type.instantiateClassFromDefaultConstructor();
        if(createdObjectOptional.isEmpty()) {
            return Optional.empty();
        }
        GameData createdObject = createdObjectOptional.get();
        try {
            createdObject.setupFromGameDataMessage(message);
        } catch (Exception e) {
            LOGGER.error("Error on getting object of type {}", message.gameDataIdString, e);
        }
        return Optional.of(createdObject);
    }
}
