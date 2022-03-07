package me.dulce.gamesite.gamesite2.rooms.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import me.dulce.gamesite.gamesite2.rooms.managers.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

@SpringBootTest
public class RoomTest {
    
    public Room getTestRoom() {
        return new Room(UUID.randomUUID(), 2, null) {
            @Override
            public GameType getGameType() {
                return null;
            }
        };
    }

    @Test
    public void userJoin_userAddedToRoomSuccessfully() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(1, room.getAllJoinedUsers().size());
        assertTrue(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenFull_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user1 = User.createGuestUser();
        User user2 = User.createGuestUser();
        User user3 = User.createGuestUser();
        room.userJoin(user1);
        room.userJoin(user2);

        //actual
        boolean actual = room.userJoin(user3);

        //assert
        assertEquals(2, room.getAllJoinedUsers().size());
        assertFalse(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();
        room.userJoin(user);

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(1, room.getAllJoinedUsers().size());
        assertFalse(actual);
    }

    @Test
    public void userJoin_userBlockedFromRoomWhenInProgress_resultsFalse() {
        //assign
        class RoomChild extends Room {
            RoomChild() {
                super(UUID.randomUUID(), 2, null);
                inProgress = true;
            }
            @Override
            public GameType getGameType() { return null; }
        }

        Room room = new RoomChild();
        User user = User.createGuestUser();

        //actual
        boolean actual = room.userJoin(user);

        //assert
        assertEquals(0, room.getAllJoinedUsers().size());
        assertFalse(actual);
    }

    @Test
    public void specatorJoin_userAddedToRoomSuccessfully() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();

        //actual
        boolean actual = room.specatorJoin(user);

        //assert
        assertEquals(1, room.getAllSpectatingUsers().size());
        assertTrue(actual);
    }

    @Test
    public void specatorJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();
        room.specatorJoin(user);

        //actual
        boolean actual = room.specatorJoin(user);

        //assert
        assertEquals(1, room.getAllSpectatingUsers().size());
        assertFalse(actual);
    }

    @Test
    public void userLeave_userRemovedFromRoomIfPlaying() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();
        room.userJoin(user);

        //actual
        room.userLeave(user);

        //assert
        assertEquals(0, room.getAllSpectatingUsers().size());
    }

    @Test
    public void userLeave_userRemovedFromRoomIfSpectating() {
        //assign
        Room room = getTestRoom();
        User user = User.createGuestUser();
        room.specatorJoin(user);

        //actual
        room.userLeave(user);

        //assert
        assertEquals(0, room.getAllSpectatingUsers().size());
    }

    @Test
    public void getRoomListingObject_RoomListingContainsSameData() {
        //assign
        User user = User.createGuestUser();
        Room room = new Room(UUID.randomUUID(), 2, user) {
            @Override
            public GameType getGameType() {
                return GameType.NULL_GAME_TYPE;
            }
        };
        String expectedUuid = room.getRoomUid().toString();
        int expectedLobbySize = room.getAllJoinedUsers().size();
        int expectedMaxLobbySize = room.getMaxUsers();
        int expectedSpectatorCount = room.getAllSpectatingUsers().size();
        String expectedGameType = room.getGameType().toString();
        String expectedHostName = room.getHost().getName();
        boolean expectedProgressState = room.getInProgressState();
        long expectedGameStartTime = room.getStartTime();

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
}
