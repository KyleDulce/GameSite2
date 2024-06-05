package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateResponse {
    public boolean success;
    public String roomId;
}
