package me.dulce.gamesite.gamesite2.rooms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

@Service
@Scope("singleton")
public class RoomManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomManager.class);

    private HashMap<UUID, Room> activeRooms = new HashMap<>();

    private final SocketMessengerService messengerService;

    public RoomManager(SocketMessengerService messengerService){

        this.messengerService = messengerService;

    }

    public RoomListing[] getAllRoomListings() {
        LinkedList<RoomListing> result = new LinkedList<>();

        for(Room room : activeRooms.values()) {
            result.add(room.getRoomListingObject());
        }

        return result.toArray(new RoomListing[result.size()]);
    }

    public boolean processUserJoinRoomRequest(User user, UUID roomId, boolean isSpectator) {
        if(user == null || roomId == null) {
            LOGGER.warn("Attempted to join user using null user or roomId credentials");
            return false;
        }

        Room room = activeRooms.get(roomId);
        if(room == null) {
            LOGGER.warn("Attempted to join user with room that does not exist");
            return false;
        }

        if(isSpectator) {
            return room.specatorJoin(user);
        } else {
            return room.userJoin(user);
        }
    }

    public void processUserLeaveRoomRequest(User user, UUID roomId) {
        if(user == null || roomId == null) {
            LOGGER.warn("Attempted to leave user using null user or roomId credentials");
            return;
        }

        Room room = activeRooms.get(roomId);
        if(room == null) {
            LOGGER.warn("Attempted to leave user with room that does not exist");
            return;
        }

        room.userLeave(user);

        if(room.isEmpty()) {
            activeRooms.remove(room.getRoomid());
        }else if(room.getHost().equals(user)){
            room.selectNewRandomHost();
        }

    }

    public UUID createRoom(GameType type, User host, int maxPlayers, String roomName) {
        UUID uuid = UUID.randomUUID();

        Room room = type.createRoomInstance(uuid, host, maxPlayers, roomName, messengerService);

        if(room == null) {
            return null;
        }

        activeRooms.put(uuid, room);
        room.userJoin(host);
        return uuid;
    }

    public UUID getRoomThatContainsUser(User user) {
        for(Room room : activeRooms.values()) {
            if(room.getUsersJoinedList().contains(user) || room.getSpectatorsJoinedList().contains(user)) {
                return room.getRoomid();
            }
        }
        return null;
    }

    public boolean isUserInRoom(User user, Room room) {
        return room.getUsersJoinedList().contains(user) || room.getSpectatorsJoinedList().contains(user);
    }

    public boolean handleIncomingRoomData(User user, GameData data) {
        return activeRooms.get(data.roomId()).handleGameDataReceived(user, data);
    }

    public Room getRoomFromUUID(UUID uuid) {
        return activeRooms.get(uuid);
    }
    public boolean doesRoomExist(UUID uuid) {
        return activeRooms.containsKey(uuid);
    }
}
