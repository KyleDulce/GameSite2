package me.dulce.gamesite.gamesite2.transportcontroller.services;

import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.InvalidSocketMessage;
import me.dulce.gamesite.gamesite2.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/** Service handling socket communication */
@Service
public class SocketMessengerService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  public static final String BROADCAST_DESTINATION = "/socket/topic/game";

  @Autowired
  public SocketMessengerService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  /**
   * Broadcasts message payload to specific room
   *
   * @param room
   * @param payload
   */
  public void broadcastMessageToRoom(Room room, Object payload) {
    simpMessagingTemplate.convertAndSend(
        BROADCAST_DESTINATION + room.getRoomId().toString(), payload);
  }

  /**
   * privately sends message to a specific user
   *
   * @param user
   * @param destination
   * @param payload
   */
  public void sendMessageToUser(User user, SocketDestinations destination, Object payload) {
    sendMessageToUser(user.getSocketId(), destination, payload);
  }

  /**
   * privately sends message to a specific user via sessionId
   *
   * @param sessionId
   * @param destination
   * @param payload
   */
  public void sendMessageToUser(String sessionId, SocketDestinations destination, Object payload) {
    SimpMessageHeaderAccessor simpMessageHeaderAccessor =
        SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    simpMessageHeaderAccessor.setSessionId(sessionId);
    simpMessageHeaderAccessor.setLeaveMutable(true);
    simpMessagingTemplate.convertAndSendToUser(
        sessionId,
        destination.getEndpoint(),
        payload,
        simpMessageHeaderAccessor.getMessageHeaders());
  }

  /**
   * sends message to user regarding invalid message
   *
   * @param user
   * @param destination
   * @param code
   * @param message
   */
  public void sendInvalidSocketMessageToUser(
      User user, SocketDestinations destination, int code, String message) {
    sendMessageToUser(user.getSocketId(), destination, buildInvalidMessageObject(code, message));
  }

  /**
   * sends message to user regarding invalid message
   *
   * @param sessionId
   * @param destination
   * @param code
   * @param message
   */
  public void sendInvalidSocketMessageToUser(
      String sessionId, SocketDestinations destination, int code, String message) {
    sendMessageToUser(sessionId, destination, buildInvalidMessageObject(code, message));
  }

  /**
   * creates invalid message for socket
   *
   * @param code
   * @param message
   * @return
   */
  public InvalidSocketMessage buildInvalidMessageObject(int code, String message) {
    InvalidSocketMessage result = new InvalidSocketMessage();
    result.code = code;
    result.message = message;
    return result;
  }

  /** All destinations for clients to receive data */
  public enum SocketDestinations {
    GAMEDATA("/gamePlayer");

    private final String endpoint;

    SocketDestinations(String endpoint) {
      this.endpoint = endpoint;
    }

    public String getEndpoint() {
      return "/queue" + endpoint;
    }
  }
}
