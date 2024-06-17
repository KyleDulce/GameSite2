package me.dulce.commongames.messaging;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoomListing {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String roomId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public int lobbySize;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public int maxLobbySize;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public int spectatorsAmount;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String gameId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String hostName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean inProgress;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public Instant gameStartTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String roomName;
}
