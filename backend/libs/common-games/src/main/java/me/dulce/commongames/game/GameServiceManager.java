package me.dulce.commongames.game;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessageHandler;
import me.dulce.commongames.messaging.ClientMessagingService;

import java.util.UUID;

public interface GameServiceManager {

    String getGameId();

    String getGameDisplayName();

    Room createRoom(
            UUID roomId,
            int maxUserCount,
            User host,
            String roomName,
            ClientMessagingService messengerService);

    GameMessageHandler getGameMessageHandler();
}
