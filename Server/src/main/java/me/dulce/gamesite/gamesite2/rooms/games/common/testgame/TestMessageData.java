package me.dulce.gamesite.gamesite2.rooms.games.common.testgame;

import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;

import java.util.UUID;

/**
 * Basic Template for Message data objects
 */
public class TestMessageData extends GameData {
    public UUID roomId;
    public String message;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public GameDataType gameDataType() {
        return GameDataType.TEST;
    }

    @Override
    public void setupFromGameDataMessage(GameDataMessage message) {
        this.roomId = UUID.fromString(message.roomId);
        this.message = (String) message.data;
    }

    @Override
    public Object onGetParse() {
        return message;
    }
}
