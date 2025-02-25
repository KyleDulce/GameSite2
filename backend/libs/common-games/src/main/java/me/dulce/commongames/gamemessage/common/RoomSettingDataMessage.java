package me.dulce.commongames.gamemessage.common;

import lombok.*;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.OutgoingGameData;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class RoomSettingDataMessage implements OutgoingGameData {
    public List<User> players;
    public String gameID;

    @Nullable public User host;

    @Override
    public String getDataIdString() {
        return "SettingDataResponse";
    }
}
