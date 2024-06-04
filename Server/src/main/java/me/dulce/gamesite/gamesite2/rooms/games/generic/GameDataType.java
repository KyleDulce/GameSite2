package me.dulce.gamesite.gamesite2.rooms.games.generic;

import java.lang.reflect.Constructor;
import java.util.Optional;
import me.dulce.gamesite.gamesite2.rooms.games.common.BlankGameData;
import me.dulce.gamesite.gamesite2.rooms.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.rooms.games.common.settings.KickPlayerData;
import me.dulce.gamesite.gamesite2.rooms.games.common.settings.SettingsDataResponse;
import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestMessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Types of Game data a Message can represent */
public enum GameDataType {
  /** Null Data type */
  NULL("Null", null),
  /** Data Type for testing purposes */
  TEST("Test", TestMessageData.class),
  /** Data For joining room */
  JOIN_ROOM("JoinRoomData", BlankGameData.class),
  /** Data for sending chat message data */
  CHAT_MESSAGE("ChatMessageData", ChatMessageData.class),
  /** Data for client requesting settings data */
  SETTINGS_DATA_REQUEST("SettingsDataRequest", BlankGameData.class),
  /** Data for server responding to data request */
  SETTINGS_DATA_RESPONSE("SettingsDataResponse", SettingsDataResponse.class),
  /** Data for requesting to kick a player */
  KICK_PLAYER("KickPlayerData", KickPlayerData.class),
  /** Data for requesting player to become host */
  CHANGE_HOST("HostChangeData", BlankGameData.class),
  /** Data for informing player they have been kicked */
  FORCE_KICK("ForceKickData", BlankGameData.class),
  ;
  private static final Logger LOGGER = LoggerFactory.getLogger(GameDataType.class);
  private final String id;
  final Class<? extends GameData> childClass;

  GameDataType(String id, Class<? extends GameData> childClass) {
    this.id = id;
    this.childClass = childClass;
  }

  public String toString() {
    return id;
  }

  public Optional<GameData> instantiateClassFromDefaultConstructor() {
    if (this == NULL) {
      return Optional.empty();
    }
    try {
      Constructor<? extends GameData> constructor = childClass.getConstructor();
      return Optional.of(constructor.newInstance());
    } catch (Exception e) {
      LOGGER.error("Error Occurred while trying to construct Game Data Object", e);
      return Optional.empty();
    }
  }

  public static GameDataType getGameDataTypeFromString(String id) {
    for (GameDataType type : GameDataType.class.getEnumConstants()) {
      if (type.id.equalsIgnoreCase(id)) {
        return type;
      }
    }
    return NULL;
  }
}
