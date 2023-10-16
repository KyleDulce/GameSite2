package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.user.User.UserMessage;

public class RoomCreateRequest {
    public int maxLobbySize;
    public int gameType;
    public String roomName;
}
