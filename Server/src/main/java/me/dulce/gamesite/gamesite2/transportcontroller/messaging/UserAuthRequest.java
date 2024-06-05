package me.dulce.gamesite.gamesite2.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequest {
    public String login;
    public String password;
}
