package me.dulce.gamesite.gamesite2.rooms.managers.games.common.testgame;

import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataType;

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
    public void setupFromGameDataMessage(GameDataMessage message) throws Exception {
        this.roomId = UUID.fromString(message.roomId);
        this.message = (String) message.data;
    }

    @Override
    public Object onGetParse() throws Exception {
        return message;
    }
}
