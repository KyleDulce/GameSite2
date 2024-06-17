package me.dulce.commongames.gamemessage.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KickPlayerMessage extends GameMessage {
    public UUID roomId;
    public User player;

    public KickPlayerMessage(GameSerializableMessage gameSerializableMessage) {
        roomId = UUID.fromString(gameSerializableMessage.roomId);
        if(!(gameSerializableMessage.data instanceof String)) {
            throw new RuntimeException("cannot parse " + getDataIdString() + " into POJO");
        }

        String data = (String) gameSerializableMessage.data;

        player = User.getUserFromUUID(UUID.fromString(data))
                .orElseThrow(() -> new RuntimeException("cannot parse " + getDataIdString() + " into POJO"));
    }

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public String getDataIdString() {
        return "common-setting-kick-player";
    }

    @Override
    public Serializable onParseData() {
        return player.getUuid().toString();
    }
}
