package me.dulce.gamesite.gamesite2.rooms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

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
    }

    public UUID createRoom(GameType type, User host, int maxPlayers) {
        UUID uuid = UUID.randomUUID();

        Room room = type.createRoomInstance(uuid, host, maxPlayers);

        if(room == null) {
            return null;
        }

        activeRooms.put(uuid, room);
        room.userJoin(host);
        return uuid;
    }

    public UUID getRoomThatContainsUser(User user) {
        for(Room room : activeRooms.values()) {
            if(room.getAllJoinedUsers().contains(user) || room.getAllSpectatingUsers().contains(user)) {
                return room.getRoomUid();
            }
        }
        return null;
    }
}
