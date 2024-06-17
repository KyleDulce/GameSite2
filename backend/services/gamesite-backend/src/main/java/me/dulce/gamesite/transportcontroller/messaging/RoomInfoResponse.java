package me.dulce.gamesite.transportcontroller.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import me.dulce.commongames.messaging.RoomListing;

@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoResponse {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public RoomListing room;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean isHost;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean joinedRoom;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean isSpectating;
}
