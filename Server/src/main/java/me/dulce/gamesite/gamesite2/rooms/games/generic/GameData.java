package me.dulce.gamesite.gamesite2.rooms.games.generic;

import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

/**
 * Class representing data a game that can be sent or received
 */
public abstract class GameData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameType.class);
    public static final String NO_VALUE_STRING = "No Value!";

    /**
     * returns the id of the room
     * @return the id of the current room
     */
    public abstract UUID roomId();

    /**
     * returns the type of game the data represents
     * @return the type of game data handles
     */
    public abstract GameDataType gameDataType();

    /**
     * Method to parse generic GameDataMessage data variable into this object
     * @param message the game data message received
     * @throws Exception when parse is not successful
     */
    public abstract void setupFromGameDataMessage(GameDataMessage message) throws Exception;

    /**
     * Method that parses this object into generic Java Object
     * @return generic object from parse
     * @throws Exception when parse is not successful
     */
    public abstract Object onGetParse() throws Exception;

    /**
     * Parses this object into a GameDataMessage
     * @return representation of this object as GameDataMessage
     */
    public GameDataMessage parseObjectToDataMessage() {
        GameDataMessage gameDataMessage = new GameDataMessage();
        gameDataMessage.roomId = roomId().toString();
        gameDataMessage.gameDataIdString = gameDataType().toString();
        try {
            gameDataMessage.data = onGetParse();
        } catch (Exception e) {
            LOGGER.error("Error on parsing object of type {}", gameDataType().toString(), e);
            gameDataMessage.data = NO_VALUE_STRING;
        }
        return gameDataMessage;
    }

    /**
     * Returns an optional of GameData parsed from a GameDataMessage
     * @param message the Message received
     * @return Parsed version of GameData
     */
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
            return Optional.empty();
        }
        return Optional.of(createdObject);
    }
}
