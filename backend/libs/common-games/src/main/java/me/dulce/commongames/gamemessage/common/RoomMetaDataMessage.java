package me.dulce.commongames.gamemessage.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessage;
import me.dulce.commongames.gamemessage.GameSerializableMessage;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RoomMetaDataMessage extends GameMessage {
    public UUID roomId;
    public List<User> players;
    public String gameID;

    @Nullable public User host;

    public RoomMetaDataMessage(GameSerializableMessage gameSerializableMessage) {
        roomId = UUID.fromString(gameSerializableMessage.roomId);
        ObjectMapper objectMapper = new ObjectMapper();
        RoomMetaDataMessageSerializable serializableMessage =
                objectMapper.convertValue(
                        gameSerializableMessage.data, RoomMetaDataMessageSerializable.class);

        players =
                Stream.of(serializableMessage.players)
                        .map(user -> User.getUserFromUUID(user.uuid))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
        Optional<User> hostOptional =
                User.getUserFromUUID(
                        serializableMessage.host != null ? serializableMessage.host.uuid : null);
        hostOptional.ifPresent(user -> host = user);
        gameID = serializableMessage.gameID;
    }

    @Override
    public UUID roomId() {
        return roomId;
    }

    @Override
    public String getDataIdString() {
        return "common-setting-data";
    }

    @Override
    public Serializable onParseData() {
        return RoomMetaDataMessageSerializable.builder()
                .players(
                        players.stream()
                                .map(User::toMessageableObject)
                                .toArray(User.UserMessage[]::new))
                .gameID(gameID)
                .host(host != null ? host.toMessageableObject() : null)
                .build();
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoomMetaDataMessageSerializable implements Serializable {
        public User.UserMessage[] players;
        public String gameID;
        public User.UserMessage host;
    }
}
