package me.dulce.gamesite.gamesite2.rooms.games.common.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;
import me.dulce.gamesite.gamesite2.user.User;

@AllArgsConstructor
@NoArgsConstructor
public class SettingsDataResponse extends GameData {
  public UUID roomId;
  public List<User> players;

  @Override
  public UUID roomId() {
    return roomId;
  }

  @Override
  public GameDataType gameDataType() {
    return GameDataType.SETTINGS_DATA_RESPONSE;
  }

  @Override
  public void setupFromGameDataMessage(GameDataMessage message) {
    SettingsDataResponse.SettingsDataResponseMessage parsedMessage =
        new ObjectMapper()
            .convertValue(message.data, SettingsDataResponse.SettingsDataResponseMessage.class);
    roomId = UUID.fromString(message.roomId);
    players = new ArrayList<>();
    for (User.UserMessage userMessage : parsedMessage.players) {
      Optional<User> user = User.getUserFromUUID(userMessage.uuid);
      user.ifPresent(value -> players.add(value));
    }
  }

  @Override
  public Object onGetParse() {
    SettingsDataResponseMessage message = new SettingsDataResponseMessage();
    message.players =
        players.stream().map(User::toMessageableObject).toList().toArray(new User.UserMessage[0]);
    return message;
  }

  public static class SettingsDataResponseMessage {
    public User.UserMessage[] players;
  }
}
