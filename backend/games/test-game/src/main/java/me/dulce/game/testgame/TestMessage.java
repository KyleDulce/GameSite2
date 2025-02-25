package me.dulce.game.testgame;

import me.dulce.commongames.gamemessage.IncomingGameData;
import me.dulce.commongames.gamemessage.OutgoingGameData;

/** Basic Template for Message data objects */
@IncomingGameData("Test-TestMessage")
public class TestMessage implements OutgoingGameData {
    public String message;

    @Override
    public String getDataIdString() {
        return "Test-TestMessage";
    }
}
