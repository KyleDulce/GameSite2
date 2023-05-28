package me.dulce.gamesite.gamesite2.rooms.managers.games.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Serializable Object for Game Data
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDataMessage {
    public String gameDataIdString;
    public String roomId;
    public Object data;
}
