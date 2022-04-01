package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.user.User.UserMessage;

public class RoomJoinRequest {
    public UserMessage user;
    public boolean asSpectator;
    public String roomId;
}
