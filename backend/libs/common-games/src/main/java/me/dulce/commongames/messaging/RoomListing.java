package me.dulce.commongames.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
    public Instant gameStartTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String roomName;
}
