package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import me.dulce.gamesite.gamesite2.rooms.Room;

public class RoomInfoResponse {
  public Room.RoomListing room;
  public boolean isHost;
  public boolean joinedRoom;
  public boolean isSpectating;
}
