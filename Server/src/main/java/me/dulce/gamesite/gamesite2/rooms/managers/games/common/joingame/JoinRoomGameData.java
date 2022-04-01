package me.dulce.gamesite.gamesite2.rooms.managers.games.common.joingame;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dulce.gamesite.gamesite2.GamesiteUtils;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataType;

import java.util.UUID;

//incoming only
public class JoinRoomGameData extends GameData {
    public UUID roomId = null;
    public boolean isSpectator = false;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public GameDataType gameDataType() {
        return GameDataType.JOIN_ROOM;
    }

    @Override
    protected void setupFromGameDataMessage(GameDataMessage message) {
        MessageObject obj = new ObjectMapper().convertValue(message.data, MessageObject.class);
        isSpectator = obj.isSpectator;
        roomId = UUID.fromString(message.roomId);
    }

    @Override
    protected Object onGetParse() throws Exception {
        MessageObject result = new MessageObject();
        result.isSpectator = isSpectator;
        return result;
    }

    private static class MessageObject {
        boolean isSpectator;
    }
}
