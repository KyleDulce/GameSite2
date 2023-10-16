package me.dulce.gamesite.gamesite2.rooms.games.common.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.user.User;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class KickPlayerData extends GameData {
    public UUID roomId;
    public User player;

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public GameDataType gameDataType() {
        return GameDataType.KICK_PLAYER;
    }

    @Override
    public void setupFromGameDataMessage(GameDataMessage message) {
        KickPlayerDataMessage parsedMessage
                = new ObjectMapper().convertValue(message.data, KickPlayerDataMessage.class);
        roomId = UUID.fromString(message.roomId);
        Optional<User> userOptional = User.getUserFromUUID(parsedMessage.player.uuid);
        if(userOptional.isEmpty()) {
            throw new IllegalArgumentException("Valid user was not provided");
        }
        player = userOptional.get();
    }

    @Override
    public Object onGetParse() {
        KickPlayerDataMessage message = new KickPlayerDataMessage();
        message.player = player.toMessageableObject();
        return message;
    }

    public static class KickPlayerDataMessage {
        public User.UserMessage player;
    }
}
