package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.user.User;

public class GameDataUpdate {
    public User.UserMessage user;
    public GameDataMessage gameData;
}