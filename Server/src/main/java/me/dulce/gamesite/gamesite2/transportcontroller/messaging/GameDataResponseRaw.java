package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.user.User;

public class GameDataResponseRaw {
    public User.UserMessage[] players;
    public int gameType;
    public User.UserMessage host;
    public GameDataMessage gameData;
}
