package me.dulce.gamesite.gamesite2.rooms.managers.games.common.joingame;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameDataType;

import java.util.UUID;

/**
 * GameData for request to join a room
 * For incoming Data only
 */
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomGameData extends GameData {
    public UUID roomId = null;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public GameDataType gameDataType() {
        return GameDataType.JOIN_ROOM;
    }

    @Override
    public void setupFromGameDataMessage(GameDataMessage message) {
        roomId = UUID.fromString(message.roomId);
    }

    @Override
    public Object onGetParse() throws Exception {
        return new MessageObject();
    }

    private static class MessageObject {
    }
}
