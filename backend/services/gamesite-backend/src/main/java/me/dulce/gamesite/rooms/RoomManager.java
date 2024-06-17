package me.dulce.gamesite.rooms;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameListing;
import me.dulce.commongames.game.GameResolver;
import me.dulce.commongames.game.GameServiceManager;
import me.dulce.commongames.gamemessage.GameSerializableMessage;
import me.dulce.commongames.gamemessage.InitialGameMessageHandler;
import me.dulce.commongames.messaging.RoomListing;
import me.dulce.gamesite.transportcontroller.services.SocketMessengerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/** Manager that manages room instances */
@Service
@Scope("singleton")
public class RoomManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomManager.class);

    private final HashMap<UUID, Room> activeRooms = new HashMap<>();

    @Autowired private SocketMessengerService messengerService;

    @Autowired private GameResolver gameResolver;

    @Autowired private InitialGameMessageHandler initialGameMessageHandler;

    /**
     * Gets all room instances as serialized listings
     *
     * @return Array containing serialized Rooms
     */
    public RoomListing[] getAllRoomListings() {
        LinkedList<RoomListing> result = new LinkedList<>();

        for (Room room : activeRooms.values()) {
            result.add(room.getRoomListingObject());
        }

        return result.toArray(new RoomListing[0]);
    }

    /**
     * Processes a request to join the room
     *
     * @param user the user making the request
     * @param roomId the room to join
     * @param isSpectator if user is a spectator
     * @return success state, true if successful
     */
    public boolean processUserJoinRoomRequest(User user, UUID roomId, boolean isSpectator) {
        if (user == null || roomId == null) {
            LOGGER.warn("Attempted to join user using null user or roomId credentials");
            return false;
        }

        Room room = activeRooms.get(roomId);
        if (room == null) {
            LOGGER.warn("Attempted to join user with room that does not exist");
            return false;
        }

        if (isSpectator) {
            return room.spectatorJoin(user);
        } else {
            return room.userJoin(user);
        }
    }

    /**
     * Processes request to leave room
     *
     * @param user user making the request
     * @param roomId the room to leave
     */
    public void processUserLeaveRoomRequest(User user, UUID roomId) {
        if (user == null || roomId == null) {
            LOGGER.warn("Attempted to leave user using null user or roomId credentials");
            return;
        }

        Room room = activeRooms.get(roomId);
        if (room == null) {
            LOGGER.warn("Attempted to leave user with room that does not exist");
            return;
        }

        room.userLeave(user);

        if (room.isEmpty()) {
            activeRooms.remove(room.getRoomId());
        }
    }

    /**
     * Creates a room
     *
     * @param host the user who is hosting the gam
     * @param maxPlayers the maximum number of players allowed
     * @param roomName the name of the room
     * @return the ID of the room created
     */
    public UUID createRoom(User host, int maxPlayers, String roomName, String gameId) {
        UUID uuid = UUID.randomUUID();

        Optional<GameServiceManager> gameServiceManager =
                gameResolver.getGameServiceManagerFromId(gameId);

        if (gameServiceManager.isEmpty()) {
            return null;
        }

        Room room =
                gameServiceManager
                        .get()
                        .createRoom(uuid, maxPlayers, host, roomName, messengerService);

        activeRooms.put(uuid, room);
        room.userJoin(host);
        return uuid;
    }

    /**
     * Finds room id that contains the given user
     *
     * @param user the user to search
     * @return the id of the room containing the user. Null otherwise
     */
    public UUID getRoomThatContainsUser(User user) {
        for (Room room : activeRooms.values()) {
            if (room.getUsersJoinedList().contains(user)
                    || room.getSpectatorsJoinedList().contains(user)) {
                return room.getRoomId();
            }
        }
        return null;
    }

    /**
     * Returns true if user is within the room
     *
     * @param user the user to search
     * @param room the room to check
     * @return true if user is in the specified room
     */
    public boolean isUserInRoom(User user, Room room) {
        return room.getUsersJoinedList().contains(user)
                || room.getSpectatorsJoinedList().contains(user);
    }

    /**
     * Handles Game Data updates for a given user
     *
     * @param sender the user making the request
     * @param data the data provided
     * @return true if successful, false otherwise
     */
    public boolean handleIncomingRoomData(User sender, GameSerializableMessage data) {
        Room room = activeRooms.get(UUID.fromString(data.roomId));
        if (room == null) {
            return false;
        }

        return initialGameMessageHandler.handleGameMessageObject(sender, data, room);
    }

    /**
     * Finds a given room from Id
     *
     * @param uuid the uuid of the room to search
     * @return the room object found, null otherwise
     */
    public Room getRoomFromUUID(UUID uuid) {
        return activeRooms.get(uuid);
    }

    /**
     * Checks if room exists in manager
     *
     * @param uuid the uuid to check
     * @return true if room exists, false otherwise
     */
    public boolean doesRoomExist(UUID uuid) {
        return activeRooms.containsKey(uuid);
    }

    public List<GameListing> getAvailableGames() {
        return gameResolver.getGameList();
    }
}
