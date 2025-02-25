package me.dulce.commongames.game;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available Game to Client")
public record GameListing(
        @Schema(description = "Technical Id for game", requiredMode = Schema.RequiredMode.REQUIRED)
                String gameId,
        @Schema(description = "Game Name to display", requiredMode = Schema.RequiredMode.REQUIRED)
                String gameDisplayName) {}
