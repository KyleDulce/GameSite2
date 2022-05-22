package me.dulce.gamesite.gamesite2.transportcontroller;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.GameDataUpdate;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.util.Optional;
import java.util.UUID;
import static me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService.SocketDestinations;

@Controller
@Scope("singleton")
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private SocketMessengerService socketMessengerService;

    @Autowired
    private RoomManager roomManager;

    @MessageMapping("/test")
    public void test(@Payload GameDataUpdate payload, @Header("simpSessionId") String sessionId) {
        LOGGER.info("RECEIVED " + sessionId);
        socketMessengerService.sendMessageToUser(sessionId, SocketDestinations.TEST, "Hello World");
    }

    @MessageMapping("/game")
    public void game(@Payload GameDataUpdate payload, @Header("simpSessionId") String sessionId) {
        if(payload.user == null) {
            socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "User Data not found! Did you follow protocol?");
            return;
        }
        try {
            UUID.fromString(payload.user.uuid);
        } catch (IllegalArgumentException e) {
            socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "User Data not found! Did you follow protocol?");
            return;
        }
        User user = User.getUserFromMessage(payload.user);
        Optional<GameData> dataOptional = GameData.getGameDataFromMessage(payload.gameData);
        if(dataOptional.isEmpty()) {
            socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "Game Data not found or invalid! Did you follow protocol?");
            return;
        }
        GameData data = dataOptional.get();
        if(!roomManager.doesRoomExist(data.roomId())) {
            socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 404, "Room not found! Did you follow protocol?");
            return;
        }

       if(!roomManager.isUserInRoom(user, roomManager.getRoomFromUUID(data.roomId()))) {
           socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 403, "User not part of the room! Join room then reconnect");
           return;
       }

        boolean success = roomManager.handleIncomingRoomData(user, data);
        if(!success) {
            socketMessengerService.sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 200, "Handler rejected Update.");
            return;
        }
    }

}
