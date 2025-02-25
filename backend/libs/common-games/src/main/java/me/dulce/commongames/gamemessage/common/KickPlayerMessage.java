package me.dulce.commongames.gamemessage.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import me.dulce.commongames.gamemessage.IncomingGameData;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IncomingGameData("SettingKickPlayer")
@Getter
public class KickPlayerMessage implements Serializable {
    private String playerUid;
}
