package me.dulce.gamesite.gamesite2.transportcontroller;

import static me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService.SocketDestinations;

import java.util.Optional;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.GameDataUpdate;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/** Controller that handles socket messages */
@Controller
@Scope("singleton")
public class SocketController {

  @Autowired private SocketMessengerService socketMessengerService;

  @Autowired private RoomManager roomManager;

  @MessageMapping("/game")
  public void game(
      @Payload GameDataUpdate payload, @Header("simpSessionId") String socketSessionId) {
    User user =
        GamesiteUtils.getUserSecurityDetailsFromPrincipal(
            SecurityContextHolder.getContext().getAuthentication());

    if (!socketSessionId.equals(user.getSocketId())) {
      user.setSocketId(socketSessionId);
    }

    Optional<GameData> dataOptional = GameData.getGameDataFromMessage(payload.gameData);
    if (dataOptional.isEmpty()) {
      socketMessengerService.sendInvalidSocketMessageToUser(
          socketSessionId,
          SocketDestinations.GAMEDATA,
          HttpStatus.BAD_REQUEST.value(),
          "Game Data not found or invalid! Did you follow protocol?");
      return;
    }

    GameData data = dataOptional.get();
    if (!roomManager.doesRoomExist(data.roomId())) {
      socketMessengerService.sendInvalidSocketMessageToUser(
          socketSessionId,
          SocketDestinations.GAMEDATA,
          HttpStatus.NOT_FOUND.value(),
          "Room not found! Did you follow protocol?");
      return;
    }

    if (!roomManager.isUserInRoom(user, roomManager.getRoomFromUUID(data.roomId()))) {
      socketMessengerService.sendInvalidSocketMessageToUser(
          socketSessionId,
          SocketDestinations.GAMEDATA,
          HttpStatus.UNAUTHORIZED.value(),
          "User not part of the room! Join room then reconnect");
      return;
    }

    boolean success = roomManager.handleIncomingRoomData(user, data);
    if (!success) {
      socketMessengerService.sendInvalidSocketMessageToUser(
          socketSessionId,
          SocketDestinations.GAMEDATA,
          HttpStatus.OK.value(),
          "Handler rejected Update.");
    }
  }
}
