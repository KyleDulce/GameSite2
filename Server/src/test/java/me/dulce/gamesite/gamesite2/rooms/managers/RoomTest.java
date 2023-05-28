package me.dulce.gamesite.gamesite2.rooms.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.UUID;

import me.dulce.gamesite.gamesite2.rooms.managers.games.common.testgame.TestGame;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.testgame.TestMessageData;
import me.dulce.gamesite.gamesite2.rooms.managers.games.generic.GameData;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.dulce.gamesite.gamesite2.rooms.managers.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

public class RoomTest {

    private static final String roomUUID_str = "5eb7a634-22a1-43c0-9d35-8b0be08b7557";
    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static final String user2UUID_str = "7095790b-7a45-462c-8fbd-9506ec6a727a";
    private static final String user3UUID_str = "15b98a25-ba8c-44f0-a5fd-0e7788a1738e";

    private static UUID roomUUID;
    private static UUID user1UUID;
    private static UUID user2UUID;
    private static UUID user3UUID;

    @BeforeAll
    public static void beforeTests() {
        roomUUID = UUID.fromString(roomUUID_str);
        user1UUID = UUID.fromString(user1UUID_str);
        user2UUID = UUID.fromString(user2UUID_str);
        user3UUID = UUID.fromString(user3UUID_str);
    }

    @Test
    public void userJoin_userAddedToRoomSuccessfully() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(1, room.getUsersJoinedList().size());
        assertTrue(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenFull_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user1 = User.createNewUser(user1UUID);
        User user2 = User.createNewUser(user2UUID);
        User user3 = User.createNewUser(user3UUID);
        room.userJoin(user1);
        room.userJoin(user2);

        //actual
        boolean actual = room.userJoin(user3);

        //assert
        assertEquals(2, room.getUsersJoinedList().size());
        assertFalse(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);
        room.userJoin(user);

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(1, room.getUsersJoinedList().size());
        assertFalse(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenInProgress_resultsFalse() {
        //assign
        class RoomChild extends Room {
            RoomChild() {
                super(roomUUID, 2, null, "test", null);
                isInProgress = true;
            }
            @Override
            public GameType getGameType() { return null; }

            @Override
            protected boolean processGameDataForGame(User user, GameData response) { return false; }
        }

        Room room = new RoomChild();
        User user = User.createNewUser(user1UUID);

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(0, room.getUsersJoinedList().size());
        assertFalse(actual);
    }

    @Test
    public void spectatorJoin_userAddedToRoomSuccessfully() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);

        //actual
        boolean actual = room.spectatorJoin(user);

        //assert
        assertEquals(1, room.getSpectatorsJoinedList().size());
        assertTrue(actual);
    }

    @Test
    public void spectatorJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);
        room.spectatorJoin(user);

        //actual
        boolean actual = room.spectatorJoin(user);

        //assert
        assertEquals(1, room.getSpectatorsJoinedList().size());
        assertFalse(actual);
    }

    @Test
    public void userLeave_userRemovedFromRoomIfPlaying() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);
        room.userJoin(user);

        //actual
        room.userLeave(user);

        //assert
        assertEquals(0, room.getSpectatorsJoinedList().size());
    }

    @Test
    public void userLeave_userRemovedFromRoomIfSpectating() {
        //assign
        Room room = getTestRoom();
        User user = User.createNewUser(user1UUID);
        room.spectatorJoin(user);

        //actual
        room.userLeave(user);

        //assert
        assertEquals(0, room.getSpectatorsJoinedList().size());
    }

    @Test
    public void getRoomListingObject_RoomListingContainsSameData() {
        //assign
        User user = User.createNewUser(user1UUID);
        Room room = new Room(roomUUID, 2, user, "test", null) {
            @Override
            public GameType getGameType() {
                return GameType.NULL_GAME_TYPE;
            }

            @Override
            protected boolean processGameDataForGame(User user, GameData response) { return false; }
        };
        String expectedUuid = room.getRoomId().toString();
        int expectedLobbySize = room.getUsersJoinedList().size();
        int expectedMaxLobbySize = room.getMaxUsers();
        int expectedSpectatorCount = room.getSpectatorsJoinedList().size();
        int expectedGameType = room.getGameType().getId();
        String expectedHostName = room.getHost().getName();
        boolean expectedProgressState = room.isInProgress();
        long expectedGameStartTime = room.getTimeStarted();

        //actual
        RoomListing actual = room.getRoomListingObject();

        //assert
        assertEquals(expectedUuid, actual.roomId);
        assertEquals(expectedLobbySize, actual.lobbySize);
        assertEquals(expectedMaxLobbySize, actual.maxLobbySize);
        assertEquals(expectedSpectatorCount, actual.spectatorsAmount);
        assertEquals(expectedGameType, actual.gameType);
        assertEquals(expectedHostName, actual.hostName);
        assertEquals(expectedProgressState, actual.inProgress);
        assertEquals(expectedGameStartTime, actual.gameStartTime);
    }

    @Test
    public void handleGameDataReceived_chatMessage_MessageIsBroadcast(){

        //assign
        SocketMessengerService messengerService = mock(SocketMessengerService.class);
        Room testRoom = new TestGame(roomUUID, 2, null, "test", messengerService);
        ChatMessageData sampleData = new ChatMessageData(testRoom.getRoomId(), "This is a message", "Bobby");

        //actual
        testRoom.handleGameDataReceived(null, sampleData);

        //assert
        verify(messengerService, times(1)).broadcastMessageToRoom(eq(testRoom), any());

    }

    @Test
    public void handleGameDataReceived_other_MessageIsProcessed() {
        SocketMessengerService messengerService = mock(SocketMessengerService.class);
        Room testRoom = new TestGame(roomUUID, 2, null, "test", messengerService);
        Room testRoomSpy = spy(testRoom);
        TestMessageData testMessageData = new TestMessageData();

        boolean actual = testRoomSpy.handleGameDataReceived(null, testMessageData);

        assertTrue(actual);
        verify(testRoomSpy, times(1)).processGameDataForGame(any(), any());
    }

    public Room getTestRoom() {
        return new Room(roomUUID, 2, null, "test", null) {
            @Override
            public GameType getGameType() {
                return null;
            }

            @Override
            protected boolean processGameDataForGame(User user, GameData response) { return false; }
        };
    }
}
