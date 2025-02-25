package me.dulce.gamesite.transportcontroller;

import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameSerializableMessage;
import me.dulce.commongames.messaging.SocketDestinations;
import me.dulce.commonutils.StringUtils;
import me.dulce.gamesite.rooms.RoomManager;
import me.dulce.gamesite.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.utilservice.UserSecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

/** Controller that handles socket messages */
@Controller
@Scope("singleton")
public class SocketController {

    @Autowired private SocketMessengerService socketMessengerService;

    @Autowired private RoomManager roomManager;

    @MessageMapping("/game")
    public void game(
            @Payload GameSerializableMessage payload,
            @Header("simpSessionId") String socketSessionId) {
        User user =
                UserSecurityUtils.getUserSecurityDetailsFromPrincipal(
                        SecurityContextHolder.getContext().getAuthentication());

        if (!socketSessionId.equals(user.getSocketId())) {
            user.setSocketId(socketSessionId);
        }

        Optional<UUID> roomId = StringUtils.getUUIDFromString(payload.roomId);

        if (roomId.isEmpty()) {
            socketMessengerService.sendInvalidSocketMessageToUser(
                    socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid Room ID");
            return;
        }

        if (!roomManager.doesRoomExist(roomId.get())) {
            socketMessengerService.sendInvalidSocketMessageToUser(
                    socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.NOT_FOUND.value(),
                    "Room not found! Did you follow protocol?");
            return;
        }

        if (!roomManager.isUserInRoom(user, roomManager.getRoomFromUUID(roomId.get()))) {
            socketMessengerService.sendInvalidSocketMessageToUser(
                    socketSessionId,
                    SocketDestinations.GAMEDATA,
                    HttpStatus.UNAUTHORIZED.value(),
                    "User not part of the room! Join room then reconnect");
            return;
        }

        roomManager.handleIncomingRoomData(user, payload);
    }
}
