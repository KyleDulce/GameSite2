package me.dulce.commongames.gamemessage.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.commongames.gamemessage.GameMessage;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class BlankGameMessage extends GameMessage {
    private UUID roomId;
    private String dataIdString;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public String getDataIdString() {
        return dataIdString;
    }

    @Override
    public Serializable onParseData() {
        return null;
    }
}
