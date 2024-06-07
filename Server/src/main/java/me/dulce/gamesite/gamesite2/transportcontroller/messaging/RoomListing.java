package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoomListing {
    public String roomId;
    public int lobbySize;
    public int maxLobbySize;
    public int spectatorsAmount;
    public String gameType;
    public String hostName;
    public boolean inProgress;
    public long gameStartTime;
    public String roomName;
}
