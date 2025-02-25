package me.dulce.commongames.gamemessage.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import me.dulce.commongames.gamemessage.OutgoingGameData;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RoomMetaDataMessage implements OutgoingGameData {
    public List<User> players;
    public String gameID;

    @Nullable public User host;

    @Override
    public String getDataIdString() {
        return "common-setting-data";
    }
}
