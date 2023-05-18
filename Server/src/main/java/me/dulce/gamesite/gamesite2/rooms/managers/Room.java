package me.dulce.gamesite.gamesite2.rooms.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

@Getter
public abstract class Room {
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);

    private UUID roomid;
    private String roomName;

    protected User host;
    protected List<User> usersJoinedList;
    protected List<User> spectatorsJoinedList;

    private int maxUsers;
    protected boolean isInProgress = false;
    protected long timeStarted = -1;

    @Getter(AccessLevel.NONE)
    protected SocketMessengerService messengerService;

    /**
     * returns the game type that the current room partakes in
     * @return the gametype of its subclass
     */
    public abstract GameType getGameType();

    /**
     * Method that handles recieved game data from user
     * @param user the user sending the data
     * @param response the data the user provides
     * @return true if successful
     */
    protected abstract boolean processGameDataForGame(User user, GameData response);

    public Room(UUID roomid, int maxUserCount, User host, String roomName, SocketMessengerService messengerService) {
        this.roomid = roomid;
        this.roomName = roomName;
        this.host = host;
        this.maxUsers = maxUserCount;
        this.messengerService = messengerService;

        usersJoinedList = new LinkedList<>();
        spectatorsJoinedList = new LinkedList<>();

        LOGGER.info("Room {} created", roomid.toString());
    }

    /**
     * Add a user to the room
     * @param user the user to add
     * @return true if successful, false if unsuccessful
     */
    public boolean userJoin(User user) {
        if(usersJoinedList.size() < maxUsers && !isInProgress && !usersJoinedList.contains(user)) {
            usersJoinedList.add(user);
            User.cachedUsers.put(user.getuuid(), user);
            return true;
        }
        return false;
    }

    /**
     * Add a user to the room as a spectator
     * @param user the user to add
     * @return true if successful, false otherwise
     */
    public boolean specatorJoin(User user) {
        if(!spectatorsJoinedList.contains(user)) {
            spectatorsJoinedList.add(user);
            User.cachedUsers.put(user.getuuid(), user);
            return true;
        }
        return false;
    }

    /**
     * Removes a user from the room
     * @param user the user to remove
     */
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

    /**
     * Randomly selects a new host from the users in the room
     */
    public void selectNewRandomHost(){
        Random rng = new Random();
        host = usersJoinedList.get(rng.nextInt(usersJoinedList.size()));
    }

    /**
     * Processes a chat message to be sent to other users in the room
     * @param chatMessage the message to send
     * @return true if successful, false otherwise
     */
    private boolean processChatMessage(ChatMessageData chatMessage){
        messengerService.broadcastMessageToRoom(this, chatMessage.parseObjectToDataMessage().data);
        return true;
    }

    public RoomListing getRoomListingObject() {
        RoomListing result = new RoomListing();
        result.roomId = roomid.toString();
        result.lobbySize = usersJoinedList.size();
        result.maxLobbySize = maxUsers;
        result.spectatorsAmount = spectatorsJoinedList.size();
        result.gameType = getGameType().getId();
        result.hostName = host.getName();
        result.inProgress = isInProgress;
        result.gameStartTime = timeStarted;
        result.roomName = roomName;

        return result;
    }

    /**
     * Returns whether the room is empty
     * @return true if room is empty, false otherwise
     */
    public boolean isEmpty(){
        return usersJoinedList.isEmpty() && spectatorsJoinedList.isEmpty();
    }

    /**
     * Serializable representation of a room
     */
    public class RoomListing {
        public String roomId;
        public int lobbySize;
        public int maxLobbySize;
        public int spectatorsAmount;
        public int gameType;
        public String hostName;
        public boolean inProgress;
        public long gameStartTime;
        public String roomName;
    }
}
