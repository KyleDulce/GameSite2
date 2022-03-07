package me.dulce.gamesite.gamesite2.rooms.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

public abstract class Room {
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);

    private UUID roomid;

    protected User host;
    protected List<User> usersJoinedList;
    protected List<User> spectatorsJoinedList;

    private int maxUsers;
    protected boolean inProgress = false;
    protected long timeStarted = -1;

    public abstract GameType getGameType();

    public Room(UUID roomid, int maxUserCount, User host) {
        this.roomid = roomid;
        this.host = host;
        this.maxUsers = maxUserCount;

        usersJoinedList = new LinkedList<>();
        spectatorsJoinedList = new LinkedList<>();

        LOGGER.info("Room {} created", roomid.toString());
    }

    public boolean userJoin(User user) {
        if(usersJoinedList.size() < maxUsers && !inProgress && !usersJoinedList.contains(user)) {
            usersJoinedList.add(user);
            User.cachedUsers.put(user.getuuid(), user);
            return true;
        }
        return false;
    }

    public boolean specatorJoin(User user) {
        if(!spectatorsJoinedList.contains(user)) {
            spectatorsJoinedList.add(user);
            User.cachedUsers.put(user.getuuid(), user);
            return true;
        }
        return false;
    }

    public void userLeave(User user) {
        if(usersJoinedList.contains(user)) {
            usersJoinedList.remove(user);
        } else if(spectatorsJoinedList.contains(user)) {
            spectatorsJoinedList.remove(user);
        }
        User.cachedUsers.remove(user.getuuid());
    }

    public UUID getRoomUid() { return roomid; }
    public User getHost() { return host; }
    public List<User> getAllJoinedUsers() { return usersJoinedList; }
    public List<User> getAllSpectatingUsers() { return spectatorsJoinedList; }
    public int getMaxUsers() { return maxUsers; }
    public boolean getInProgressState() { return inProgress; }
    public long getStartTime() { return timeStarted; }

    public RoomListing getRoomListingObject() {
        RoomListing result = new RoomListing();
        result.roomId = roomid.toString();
        result.lobbySize = usersJoinedList.size();
        result.maxLobbySize = maxUsers;
        result.spectatorsAmount = spectatorsJoinedList.size();
        result.gameType = getGameType().toString();
        result.hostName = host.getName();
        result.inProgress = inProgress;
        result.gameStartTime = timeStarted;

        return result;
    }

    public class RoomListing {
        public String roomId;
        public int lobbySize;
        public int maxLobbySize;
        public int spectatorsAmount;
        public String gameType;
        public String hostName;
        public boolean inProgress;
        public long gameStartTime;
    }
}
