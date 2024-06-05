package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.Room;

@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoResponse {
    public Room.RoomListing room;
    public boolean isHost;
    public boolean joinedRoom;
    public boolean isSpectating;
}
