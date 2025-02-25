package me.dulce.commongames;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import me.dulce.commongames.gamemessage.GameMessengerService;
import me.dulce.commongames.gamemessage.OutgoingGameData;
import me.dulce.commongames.messaging.RoomListing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

public class RoomTest {

    private static final String roomUUID_str = "5eb7a634-22a1-43c0-9d35-8b0be08b7557";
    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static final String user2UUID_str = "7095790b-7a45-462c-8fbd-9506ec6a727a";
    private static final String user3UUID_str = "15b98a25-ba8c-44f0-a5fd-0e7788a1738e";
    private static final String hostUserUUID_str = "3fac37e7-aff8-4a1f-879d-2e32eeb28ba6";

    private static final String sampleGameId = "someGameId";

    private static UUID roomUUID;
    private static UUID user1UUID;
    private static UUID user2UUID;
    private static UUID user3UUID;
    private static UUID hostUserUUID;

    private static final String hostName = "SomeHose";
    private static final String user1Name = "SomeUser1";
    private static final String user2Name = "SomeUser2";
    private static final String user3Name = "SomeUser3";

    private static final String hostSession = "SomeSessionHost";
    private static final String user1Session = "SomeSession1";
    private static final String user2Session = "SomeSession2";
    private static final String user3Session = "SomeSession3";

    private GameMessengerService gameMessengerService;

    @BeforeAll
    public static void beforeTests() {
        roomUUID = UUID.fromString(roomUUID_str);
        user1UUID = UUID.fromString(user1UUID_str);
        user2UUID = UUID.fromString(user2UUID_str);
        user3UUID = UUID.fromString(user3UUID_str);
        hostUserUUID = UUID.fromString(hostUserUUID_str);
    }

    @BeforeEach
    public void beforeEachTest() {
        gameMessengerService = mock(GameMessengerService.class);
    }

    @AfterEach
    public void afterEachTest() {
        User.getCachedUsers().clear();
    }

    @Test
    public void userJoin_userAddedToRoomSuccessfully() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);

        // actual
        boolean actual = room.userJoin(user);

        // assert
        assertEquals(1, room.getUsersJoinedList().size());
        assertTrue(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenFull_resultsFalse() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user1 = User.createNewUser(user1UUID, user1Name, user1Session);
        User user2 = User.createNewUser(user2UUID, user2Name, user2Session);
        User user3 = User.createNewUser(user3UUID, user3Name, user3Session);
        room.userJoin(user1);
        room.userJoin(user2);

        // actual
        boolean actual = room.userJoin(user3);

        // assert
        assertEquals(2, room.getUsersJoinedList().size());
        assertFalse(actual);
        verify(gameMessengerService, times(2)).sendToUser(any(User.class), any(Room.class), any(OutgoingGameData.class));
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        room.userJoin(user);

        // actual
        boolean actual = room.userJoin(user);

        // assert
        assertEquals(1, room.getUsersJoinedList().size());
        assertFalse(actual);
        verify(gameMessengerService, atMostOnce())
                .sendToUser(any(User.class), any(Room.class), any(OutgoingGameData.class));
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenInProgress_resultsFalse() {
        // assign
        Room room =
                getRoom(roomUUID, 2, User.createNewUser(roomUUID, "name", "session"), "test", null);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        room.setInProgress();

        // actual
        boolean actual = room.userJoin(user);

        // assert
        assertEquals(0, room.getUsersJoinedList().size());
        assertFalse(actual);
        verify(gameMessengerService, never()).sendToUser(any(User.class), any(Room.class), any(OutgoingGameData.class));
    }

    @Test
    public void spectatorJoin_userAddedToRoomSuccessfully() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);

        // actual
        boolean actual = room.spectatorJoin(user);

        // assert
        assertEquals(1, room.getSpectatorsJoinedList().size());
        assertTrue(actual);
    }

    @Test
    public void spectatorJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        room.spectatorJoin(user);

        // actual
        boolean actual = room.spectatorJoin(user);

        // assert
        assertEquals(1, room.getSpectatorsJoinedList().size());
        assertFalse(actual);
    }

    @Test
    public void userLeave_userRemovedFromRoomIfPlaying() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        room.userJoin(user);

        // actual
        room.userLeave(user);

        // assert
        assertEquals(0, room.getSpectatorsJoinedList().size());
    }

    @Test
    public void userLeave_userRemovedFromRoomIfSpectating() {
        // assign
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        room.spectatorJoin(user);

        // actual
        room.userLeave(user);

        // assert
        assertEquals(0, room.getSpectatorsJoinedList().size());
        verify(gameMessengerService, never()).sendToUser(any(User.class), any(Room.class), any(OutgoingGameData.class));
    }

    @Test
    public void userLeave_newHostWhenHostRemoved() {
        // assign
        User host = User.createNewUser(hostUserUUID, hostName, hostSession);
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        Room room = getRoom(roomUUID, 2, host, "test", gameMessengerService);
        room.userJoin(host);
        room.userJoin(user);

        // actual
        room.userLeave(host);

        // assert
        assertEquals(1, room.getUsersJoinedList().size());
        assertEquals(user, room.host);
        verify(gameMessengerService, times(3)).sendToUser(any(User.class), any(Room.class), any(OutgoingGameData.class));
        verify(gameMessengerService, times(1)).sendToUser(any(User.class), any(Room.class), anyString());
    }

    @Test
    public void getRoomListingObject_RoomListingContainsSameData() {
        // assign
        User user = User.createNewUser(user1UUID, user1Name, user1Session);
        Room room = getRoom(roomUUID, 2, user, "test", null);
        String expectedUuid = room.getRoomId().toString();
        int expectedLobbySize = room.getUsersJoinedList().size();
        int expectedMaxLobbySize = room.getMaxUsers();
        int expectedSpectatorCount = room.getSpectatorsJoinedList().size();

        String expectedHostName = room.getHost().getName();
        boolean expectedProgressState = room.isInProgress();
        Instant expectedGameStartTime = room.getTimeStarted();

        // actual
        RoomListing actual = room.getRoomListingObject();

        // assert
        assertEquals(expectedUuid, actual.roomId);
        assertEquals(expectedLobbySize, actual.lobbySize);
        assertEquals(expectedMaxLobbySize, actual.maxLobbySize);
        assertEquals(expectedSpectatorCount, actual.spectatorsAmount);
        assertEquals(expectedHostName, actual.hostName);
        assertEquals(expectedProgressState, actual.inProgress);
        assertEquals(expectedGameStartTime, actual.gameStartTime);
    }

    private Room getRoom(
            UUID roomID,
            int maxUserCount,
            User user,
            String name,
            GameMessengerService messengerService) {
        return new Room(roomID, maxUserCount, user, name, messengerService) {
            @Override
            public String getGameId() {
                return sampleGameId;
            }

            @Override
            protected void onUserJoinEvent(User user) {}

            @Override
            protected void onSpectatorJoinEvent(User user) {}

            @Override
            protected void onUserLeaveEvent(User user) {}
        };
    }
}
