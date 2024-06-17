package me.dulce.commongames.messaging;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;

public interface ClientMessagingService {
    /**
     * Broadcasts message payload to specific room
     *
     * @param room
     * @param payload
     */
    void broadcastMessageToRoom(Room room, Object payload);

    /**
     * privately sends message to a specific user
     *
     * @param user
     * @param destination
     * @param payload
     */
    void sendMessageToUser(User user, SocketDestinations destination, Object payload);

    /**
     * privately sends message to a specific user via sessionId
     *
     * @param sessionId
     * @param destination
     * @param payload
     */
    void sendMessageToUser(String sessionId, SocketDestinations destination, Object payload);

    /**
     * sends message to user regarding invalid message
     *
     * @param user
     * @param destination
     * @param code
     * @param message
     */
    void sendInvalidSocketMessageToUser(
            User user, SocketDestinations destination, int code, String message);

    /**
     * sends message to user regarding invalid message
     *
     * @param sessionId
     * @param destination
     * @param code
     * @param message
     */
    void sendInvalidSocketMessageToUser(
            String sessionId, SocketDestinations destination, int code, String message);

    /**
     * creates invalid message for socket
     *
     * @param code
     * @param message
     * @return
     */
    InvalidSocketMessage buildInvalidMessageObject(int code, String message);
}
