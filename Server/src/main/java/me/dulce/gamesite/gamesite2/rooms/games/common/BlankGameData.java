package me.dulce.gamesite.gamesite2.rooms.games.common;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataMessage;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;

@NoArgsConstructor
@AllArgsConstructor
public class BlankGameData extends GameData {
  public UUID roomId;
  public GameDataType gameDataType;

  @Override
  public UUID roomId() {
    return roomId;
  }

  @Override
  public GameDataType gameDataType() {
    return gameDataType;
  }

  @Override
  public void setupFromGameDataMessage(GameDataMessage message) {
    roomId = UUID.fromString(message.roomId);
    gameDataType = GameDataType.getGameDataTypeFromString(message.gameDataIdString);
  }

  @Override
  public Object onGetParse() {
    return null;
  }
}
