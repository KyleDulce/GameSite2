package me.dulce.commongames.gamemessage;

import java.io.Serializable;

public interface OutgoingGameData extends Serializable {
    public abstract String getDataIdString();
}
