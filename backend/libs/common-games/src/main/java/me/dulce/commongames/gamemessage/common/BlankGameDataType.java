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
public enum BlankGameDataType {

    /** Data for client requesting settings data */
    SETTINGS_DATA_REQUEST("SettingsDataRequest"),
    /** Data for requesting player to become host */
    CHANGE_HOST("HostChangeData"),
    /** Data for informing player they have been kicked */
    FORCE_KICK("ForceKickData"),
    ;

    private static final Map<String, BlankGameDataType> commonGameDataMapping =
            Stream.of(BlankGameDataType.values())
                    .collect(Collectors.toMap(BlankGameDataType::getDataId, item -> item));

    private final String dataId;

    public String toString() {
        return dataId;
    }

    public static Optional<BlankGameDataType> getTypeFromId(@NotNull String id) {
        return Optional.ofNullable(commonGameDataMapping.get(id));
    }
}
