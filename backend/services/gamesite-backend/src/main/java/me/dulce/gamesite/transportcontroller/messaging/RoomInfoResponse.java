package me.dulce.gamesite.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import me.dulce.commongames.messaging.RoomListing;

@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoResponse {
    public RoomListing room;
    public boolean isHost;
    public boolean joinedRoom;
    public boolean isSpectating;
}
