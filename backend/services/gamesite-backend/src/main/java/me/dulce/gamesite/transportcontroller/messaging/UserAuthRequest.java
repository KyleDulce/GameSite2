package me.dulce.gamesite.transportcontroller.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequest {
    public String login;
    public String password;
}
