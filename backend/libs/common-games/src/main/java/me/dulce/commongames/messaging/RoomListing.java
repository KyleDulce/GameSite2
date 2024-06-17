package me.dulce.commongames.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.dulce.commongames.Room;

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
