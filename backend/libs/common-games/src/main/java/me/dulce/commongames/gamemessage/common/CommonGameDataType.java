package me.dulce.commongames.gamemessage.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum CommonGameDataType {

    /** Data for sending chat message data */
    CHAT_MESSAGE("ChatMessageData"),
    /** Data for client requesting settings data */
    SETTINGS_DATA_REQUEST("SettingsDataRequest"),
    /** Data for server responding to data request */
    SETTINGS_DATA_RESPONSE("SettingsDataResponse"),
    /** Data for requesting to kick a player */
    KICK_PLAYER("KickPlayerData"),
    /** Data for requesting player to become host */
    CHANGE_HOST("HostChangeData"),
    /** Data for informing player they have been kicked */
    FORCE_KICK("ForceKickData"),
    ;

    private static final Map<String, CommonGameDataType> commonGameDataMapping =
            Stream.of(CommonGameDataType.values())
                    .collect(Collectors.toMap(CommonGameDataType::getDataId, item -> item));

    private final String dataId;

    public String toString() {
        return dataId;
    }

    public static Optional<CommonGameDataType> getTypeFromId(@NotNull String id) {
        return Optional.ofNullable(commonGameDataMapping.get(id));
    }
}
