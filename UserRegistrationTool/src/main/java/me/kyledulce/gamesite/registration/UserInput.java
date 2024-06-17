package me.kyledulce.gamesite.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserInput {
    private String username;
    private String password;
    private List<String> roles;
}
