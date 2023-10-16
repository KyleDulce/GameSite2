package me.dulce.gamesite.gamesite2.transportcontroller;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.GameDataUpdate;
import me.dulce.gamesite.gamesite2.transportcontroller.services.CookieService;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.util.Optional;

import static me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService.SocketDestinations;

/**
 * Controller that handles socket messages
 */
@Controller
@Scope("singleton")
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private SocketMessengerService socketMessengerService;

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private CookieService cookieService;

    @MessageMapping("/game")
    public void game(@Payload GameDataUpdate payload, @Header("simpSessionId") String socketSessionId) {
        Optional<User> userOptional = cookieService.validateUserCookie(payload.authToken);
        if(userOptional.isEmpty()) {
            socketMessengerService.sendInvalidSocketMessageToUser(socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid Auth Token! Try logging in again!");
            return;
        }

        if(!socketSessionId.equals(userOptional.get().getSocketId())) {
            userOptional.get().setSocketId(socketSessionId);
        }

        Optional<GameData> dataOptional = GameData.getGameDataFromMessage(payload.gameData);
        if(dataOptional.isEmpty()) {
            socketMessengerService.sendInvalidSocketMessageToUser(socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.BAD_REQUEST.value(),
                    "Game Data not found or invalid! Did you follow protocol?");
            return;
        }

        GameData data = dataOptional.get();
        if(!roomManager.doesRoomExist(data.roomId())) {
            socketMessengerService.sendInvalidSocketMessageToUser(socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.NOT_FOUND.value(),
                    "Room not found! Did you follow protocol?");
            return;
        }

       if(!roomManager.isUserInRoom(userOptional.get(), roomManager.getRoomFromUUID(data.roomId()))) {
           socketMessengerService.sendInvalidSocketMessageToUser(socketSessionId,
                   SocketDestinations.GAMEDATA,
                   HttpStatus.UNAUTHORIZED.value(),
                   "User not part of the room! Join room then reconnect");
           return;
       }

        boolean success = roomManager.handleIncomingRoomData(userOptional.get(), data);
        if(!success) {
            socketMessengerService.sendInvalidSocketMessageToUser(socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.OK.value(),
                    "Handler rejected Update.");
        }
    }

}
