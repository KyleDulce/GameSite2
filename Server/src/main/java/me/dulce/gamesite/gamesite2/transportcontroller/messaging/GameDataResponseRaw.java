package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.user.User;

public class GameDataResponseRaw {
  public User.UserMessage[] players;
  public String gameType;
  public User.UserMessage host;
  public GameDataMessage gameData;
}
