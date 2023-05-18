package me.dulce.gamesite.gamesite2.transportcontroller.services;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.InvalidSocketMessage;
import me.dulce.gamesite.gamesite2.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketMessengerService {

    private SimpMessagingTemplate simpMessagingTemplate;
    public static final String BROADCAST_DESTINATION = "/socket/topic/game";

    @Autowired
    public SocketMessengerService(SimpMessagingTemplate simpMessagingTemplate){

        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    public void broadcastMessageToRoom(Room room, Object payload) {
        simpMessagingTemplate.convertAndSend(BROADCAST_DESTINATION + room.getRoomid().toString(), payload);
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
