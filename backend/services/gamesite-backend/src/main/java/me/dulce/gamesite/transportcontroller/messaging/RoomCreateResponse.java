package me.dulce.gamesite.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateResponse {
    public boolean success;
    public String roomId;
}
