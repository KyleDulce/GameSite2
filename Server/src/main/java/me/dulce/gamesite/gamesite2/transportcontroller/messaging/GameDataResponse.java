package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameDataResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameDataResponse.class);

    public User[] players;
    public GameType gameType;
    public User host;
    public GameData gameData;

    public GameDataResponseRaw toRawResponse() {
        GameDataResponseRaw result = new GameDataResponseRaw();

        User.UserMessage[] resultPlayers = new User.UserMessage[this.players.length];
        for(int p = 0; p < players.length; p++) {
            resultPlayers[p] = players[p].toMessageableObject();
        }
        result.players = resultPlayers;
        result.gameType = gameType.getId();
        result.host = host.toMessageableObject();
        result.gameData = gameData.parseObjectToDataMessage();

        return result;
    }
}
