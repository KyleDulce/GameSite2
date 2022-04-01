package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class GameDataResponse {
    private static Logger LOGGER = LoggerFactory.getLogger(GameDataResponse.class);

    public User[] players;
    public GameType gameType;
    public User host;
    public GameData gameData;

    public GameDataResponseRaw toRawResponse() {
        GameDataResponseRaw result = new GameDataResponseRaw();

        User.UserMessage[] resultPlayers = new User.UserMessage[this.players.length];
        for(int p = 0; p < players.length; p++) {
            resultPlayers[p] = players[p].toMessagableObject();
        }
        result.players = resultPlayers;
        result.gameType = gameType.getId();
        result.host = host.toMessagableObject();
        result.gameData = gameData.parseObjectToDataMessage();

        return result;
    }

    public static GameDataResponse fromRawResponse(GameDataResponseRaw gameDataResponseRaw) {
        GameDataResponse result = new GameDataResponse();
        result.players = new User[gameDataResponseRaw.players.length];
        for(int i = 0; i < gameDataResponseRaw.players.length; i++) {
            result.players[i] = User.getUserFromMessage(gameDataResponseRaw.players[i]);
        }
        result.gameType = GameType.getGameTypeFromId(gameDataResponseRaw.gameType);
        result.host = User.getUserFromMessage(gameDataResponseRaw.host);

        Optional<GameData> gameDataOptional = GameData.getGameDataFromMessage(gameDataResponseRaw.gameData);
        if(gameDataOptional.isEmpty()) {
            result.gameData = null;
            LOGGER.warn("Unable to get Game Data from Message");
        } else {
            result.gameData = gameDataOptional.get();
        }
        return result;
    }
}
