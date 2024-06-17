package me.dulce.commongames.gamemessage;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class GameSerializableMessage {
    public String gameDataIdString;
    public String roomId;
    public Serializable data;
}
