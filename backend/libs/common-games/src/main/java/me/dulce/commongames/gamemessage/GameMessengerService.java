package me.dulce.commongames.gamemessage;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.messaging.ClientMessagingService;
import me.dulce.commongames.messaging.SocketDestinations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameMessengerService {
    @Autowired private ClientMessagingService clientMessagingService;

    public void broadcastToRoom(Room room, String dataIdString) {
        broadcastToRoom(room, dataIdString, null);
    }

    public void broadcastToRoom(Room room, OutgoingGameData gameData) {
        broadcastToRoom(room, gameData.getDataIdString(), gameData);
    }

    public void broadcastToRoom(Room room, String dataIdString, OutgoingGameData gameData) {
        clientMessagingService.broadcastMessageToRoom(
                room,
                new GameSerializableMessage(dataIdString, room.getRoomId().toString(), gameData));
    }

    public void sendToUser(User user, Room room, String dataIdString) {
        sendToUser(user, room, dataIdString, null);
    }

    public void sendToUser(User user, Room room, OutgoingGameData gameData) {
        sendToUser(user, room, gameData.getDataIdString(), gameData);
    }

    public void sendToUser(User user, Room room, String dataIdString, OutgoingGameData gameData) {
        clientMessagingService.sendMessageToUser(
                user,
                SocketDestinations.GAMEDATA,
                new GameSerializableMessage(dataIdString, room.getRoomId().toString(), gameData));
    }

    public void sendInvalidMessageToUser(User user, int code, String message) {
        clientMessagingService.sendInvalidSocketMessageToUser(
                user, SocketDestinations.GAMEDATA, code, message);
    }
}
