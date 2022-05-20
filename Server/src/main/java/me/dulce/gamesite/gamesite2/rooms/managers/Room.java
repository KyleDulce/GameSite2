package me.dulce.gamesite.gamesite2.rooms.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.SocketController;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
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
    protected SocketMessengerService messengerService;

    public abstract GameType getGameType();
    protected abstract boolean processGameDataForGame(User user, GameData response);

    public Room(UUID roomid, int maxUserCount, User host, SocketMessengerService messengerService) {
        this.roomid = roomid;
        this.host = host;
        this.maxUsers = maxUserCount;
        this.messengerService = messengerService;

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

    /**
     * This method is for common logic among games for handling GameData such as broadcasting chat messages.
     * A separate protected method, processGameDataForGame is called to handle game specific GameData processing.
     * @param user The user sending the gameData
     * @param response The GameData from the user
     * @return A boolean indicating if the response was successful
     */
    public final boolean handleGameDataReceived(User user, GameData response){

        if(response instanceof ChatMessageData){
            return processChatMessage((ChatMessageData) response)
                    && processGameDataForGame(user, response);
        }

        return processGameDataForGame(user, response);

    }

    private boolean processChatMessage(ChatMessageData chatMessage){

        messengerService.broadcastMessageToRoom(this, chatMessage.parseObjectToDataMessage().data);

        return true;

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
