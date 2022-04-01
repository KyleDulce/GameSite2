package me.dulce.gamesite.gamesite2.rooms.managers.games.generic;

import me.dulce.gamesite.gamesite2.rooms.managers.games.common.joingame.JoinRoomGameData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Optional;

public enum GameDataType {
    NULL("Null", null),

    JOIN_ROOM("JoinRoomData", JoinRoomGameData.class)
    ;
    private static Logger LOGGER = LoggerFactory.getLogger(GameDataType.class);
    private String id;
    Class<? extends GameData> childClass;
    GameDataType(String id, Class<? extends GameData> childClass) {
        this.id = id;
        this.childClass = childClass;
    }
    public String toString() {
        return id;
    }

    public Optional<GameData> instantiateClassFromDefaultConstructor() {
        if(this == NULL){
            return Optional.empty();
        }
        try {
            Constructor<? extends GameData> constructor = childClass.getConstructor();
            return Optional.of(constructor.newInstance());
        } catch (Exception e) {
            LOGGER.error("Error Occured while trying to construct Game Data Object", e);
            return Optional.empty();
        }
    }

    public static GameDataType getGameDataTypeFromString(String id) {
        for(GameDataType type : GameDataType.class.getEnumConstants()) {
            if(type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return NULL;
    }
}
