package me.dulce.gamesite.gamesite2.transportcontroller;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.GameDataUpdate;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.InvalidSocketMessage;
import me.dulce.gamesite.gamesite2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

@Controller
@Scope("singleton")
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);
    private static final String BROADCAST_DESTINATION = "/socket/topic/game";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RoomManager roomManager;

    @MessageMapping("/test")
    public void test(@Payload GameDataUpdate payload, @Header("simpSessionId") String sessionId) {
        LOGGER.info("RECEIVED " + sessionId);
        sendMessageToUser(sessionId, SocketDestinations.TEST, "Hello World");
    }

    @MessageMapping("/game")
    public void game(@Payload GameDataUpdate payload, @Header("simpSessionId") String sessionId) {
        if(payload.user == null) {
            sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "User Data not found! Did you follow protocol?");
            return;
        }
        try {
            UUID.fromString(payload.user.uuid);
        } catch (IllegalArgumentException e) {
            sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "User Data not found! Did you follow protocol?");
            return;
        }
        User user = User.getUserFromMessage(payload.user);
        Optional<GameData> dataOptional = GameData.getGameDataFromMessage(payload.gameData);
        if(dataOptional.isEmpty()) {
            sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 400, "Game Data not found or invalid! Did you follow protocol?");
            return;
        }
        GameData data = dataOptional.get();
        if(!roomManager.doesRoomExist(data.roomId())) {
            sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 404, "Room not found! Did you follow protocol?");
            return;
        }

       if(!roomManager.isUserInRoom(user, roomManager.getRoomFromUUID(data.roomId()))) {
           sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 403, "User not part of the room! Join room then reconnect");
           return;
       }

        boolean success = roomManager.handleIncomingRoomData(user, data);
        if(!success) {
            sendInvalidSocketMessageToUser(sessionId, SocketDestinations.GAMEDATA, 200, "Handler rejected Update.");
            return;
        }
    }

    public void broadcastMessageToRoom(Room room, Object payload) {
        simpMessagingTemplate.convertAndSend(BROADCAST_DESTINATION + room.getRoomUid().toString(), payload);
    }

    public void sendMessageToUser(User user, SocketDestinations destination, Object payload) {
        sendMessageToUser(user.getSessionId(),destination,payload);
    }

    public void sendMessageToUser(String sessionId, SocketDestinations destination, Object payload) {
        SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        simpMessageHeaderAccessor.setSessionId(sessionId);
        simpMessageHeaderAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSendToUser(sessionId, destination.getEndpoint(), payload, simpMessageHeaderAccessor.getMessageHeaders());
    }

    public void sendInvalidSocketMessageToUser(String sessionId, SocketDestinations destination, int code, String message) {
        sendMessageToUser(sessionId, destination, buildInvalidMessageObject(code, message));
    }

    public InvalidSocketMessage buildInvalidMessageObject(int code, String message) {
        InvalidSocketMessage result = new InvalidSocketMessage();
        result.code = code;
        result.message = message;
        return result;
    }

    public enum SocketDestinations {
        GAMEDATA("/gamePlayer"),
        TEST("/test");

        private String endpoint;
        SocketDestinations(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getEndpoint() {
            return "/queue" + endpoint;
        }
    }

}
