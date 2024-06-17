package me.dulce.gamesite.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinResponse {
    public boolean success;
    public boolean isHost;
}
