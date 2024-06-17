package me.dulce.gamesite.transportcontroller.messaging;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String login;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public String password;
}
