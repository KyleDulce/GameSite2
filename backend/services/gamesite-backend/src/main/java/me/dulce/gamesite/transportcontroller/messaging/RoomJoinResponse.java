package me.dulce.gamesite.transportcontroller.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinResponse {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean success;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public boolean isHost;
}
