package me.dulce.commongames;

import lombok.AccessLevel;
import lombok.Getter;

import me.dulce.commongames.gamemessage.common.*;
import me.dulce.commongames.messaging.ClientMessagingService;
import me.dulce.commongames.messaging.RoomListing;
import me.dulce.commongames.messaging.SocketDestinations;
import me.dulce.commonutils.StringUtils;

import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/** Room instance for games */
@Getter
public abstract class Room {
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);
    private static final int HTTP_FORBIDDEN = 403;

    private final UUID roomId;
    private final String roomName;

    protected User host;
    protected final List<User> usersJoinedList;
    protected final List<User> spectatorsJoinedList;

    private final int maxUsers;
    protected boolean isInProgress = false;
    protected final long timeStarted = -1;

    @Getter(AccessLevel.NONE)
    protected final ClientMessagingService messengerService;

    public Room(
            UUID roomId,
            int maxUserCount,
            User host,
            String roomName,
            ClientMessagingService messengerService) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.host = host;
        this.maxUsers = maxUserCount;
        this.messengerService = messengerService;

        usersJoinedList = new LinkedList<>();
        spectatorsJoinedList = new LinkedList<>();

        LOGGER.info("Room {} created", roomId.toString());
    }

    public abstract String getGameId();

    protected abstract void onUserJoinEvent(User user);

    protected abstract void onSpectatorJoinEvent(User user);

    protected abstract void onUserLeaveEvent(User user);

    /**
     * Add a user to the room
     *
     * @param user the user to add
     * @return true if successful, false if unsuccessful
     */
    public boolean userJoin(User user) {
        if (usersJoinedList.size() < maxUsers && !isInProgress && !usersJoinedList.contains(user)) {
            usersJoinedList.add(user);
            sendDataResponseUpdate();
            onUserJoinEvent(user);
            return true;
        }
        return false;
    }

    /**
     * Add a user to the room as a spectator
     *
     * @param user the user to add
     * @return true if successful, false otherwise
     */
    public boolean spectatorJoin(User user) {
        if (!spectatorsJoinedList.contains(user)) {
            spectatorsJoinedList.add(user);
            onSpectatorJoinEvent(user);
            return true;
        }
        return false;
    }

    /**
     * Removes a user from the room
     *
     * @param user the user to remove
     */
    public void userLeave(User user) {
        if (usersJoinedList.contains(user)) {
            usersJoinedList.remove(user);
            if (user.equals(host)) {
                this.selectNewRandomHost();
            }
            sendDataResponseUpdate();
        } else if (spectatorsJoinedList.contains(user)) {
            spectatorsJoinedList.remove(user);
        }
        onUserLeaveEvent(user);
    }

    /** Randomly selects a new host from the users in the room */
    protected void selectNewRandomHost() {
        if (usersJoinedList.size() <= 0) {
            return;
        }
        Random rng = new Random();
        host = usersJoinedList.get(rng.nextInt(usersJoinedList.size()));
        messengerService.sendMessageToUser(
                host,
                SocketDestinations.GAMEDATA,
                new BlankGameMessage(getRoomId(), CommonGameDataType.CHANGE_HOST.toString())
                        .parseToSerializableObject());
    }

    /**
     * Processes a chat message to be sent to other users in the room
     *
     * @param chatMessage the message to send
     * @return true if successful, false otherwise
     */
    public boolean processChatMessage(ChatMessageMessage chatMessage, User user) {
        chatMessage.setSenderName(user.getName());
        messengerService.broadcastMessageToRoom(this, chatMessage.onParseData());
        return true;
    }

    public boolean processSettingsDataRequest(User user) {
        if (!user.equals(host)) {
            sendInvalidMessageDueToNotHost(user);
            return false;
        }
        sendDataResponseUpdate();
        return true;
    }

    public boolean processPlayerKickRequest(User user, KickPlayerMessage data) {
        if (!user.equals(host)) {
            sendInvalidMessageDueToNotHost(user);
            return false;
        }
        userLeave(data.player);
        if (StringUtils.isNotBlank(data.player.getSocketId())) {
            messengerService.sendMessageToUser(
                    data.player,
                    SocketDestinations.GAMEDATA,
                    new BlankGameMessage(getRoomId(), CommonGameDataType.FORCE_KICK.toString())
                            .parseToSerializableObject());
        }
        return true;
    }

    protected void sendDataResponseUpdate() {
        messengerService.sendMessageToUser(
                host,
                SocketDestinations.GAMEDATA,
                new RoomMetaDataMessage(getRoomId(), usersJoinedList, getGameId(), host)
                        .parseToSerializableObject());
    }

    protected void sendInvalidMessageDueToNotHost(User user) {
        messengerService.sendInvalidSocketMessageToUser(
                user,
                SocketDestinations.GAMEDATA,
                HTTP_FORBIDDEN,
                "Only Host can make this request");
    }

    /**
     * Convert Room object to a listing serializable object
     *
     * @return the serializable listing object
     */
    public RoomListing getRoomListingObject() {
        RoomListing result = new RoomListing();
        result.roomId = roomId.toString();
        result.lobbySize = usersJoinedList.size();
        result.maxLobbySize = maxUsers;
        result.spectatorsAmount = spectatorsJoinedList.size();
        result.gameType = getGameId();
        result.hostName = host.getName();
        result.inProgress = isInProgress;
        result.gameStartTime = timeStarted;
        result.roomName = roomName;

        return result;
    }

    /**
     * Returns whether the room is empty
     *
     * @return true if room is empty, false otherwise
     */
    public boolean isEmpty() {
        return usersJoinedList.isEmpty();
    }

    @TestOnly
    public void setInProgress() {
        this.isInProgress = true;
    }
}
