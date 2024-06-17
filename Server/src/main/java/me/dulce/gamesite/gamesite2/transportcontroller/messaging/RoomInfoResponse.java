package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoResponse {
    public RoomListing room;
    public boolean isHost;
    public boolean joinedRoom;
    public boolean isSpectating;
}
