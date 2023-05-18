package me.dulce.gamesite.gamesite2.rooms;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomManagerTest {

    @Autowired
    private RoomManager roomManager;

    @Test
    public void allUsersLeave_RoomDoesNotExist(){

        //assign
        User testUser = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, testUser, 5, "test");

        //actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        //assert
        assertFalse(roomManager.doesRoomExist(roomId));

    }

    @Test
    public void lastUserLeaves_GetRoomThatContainsUserNull(){

        //assign
        User testUser = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, testUser, 5, "test");

        //actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        //assert
        assertNull(roomManager.getRoomThatContainsUser(testUser));

    }

    @Test
    public void hostLeaves_NewHostChosen(){

        //assign
        User[] users = new User[5];
        for(int i = 0; i < 5; i++){
            users[i] = User.createGuestUser();
        }

        UUID roomId = roomManager.createRoom(GameType.TEST, users[0], 5, "test");
        Room room = roomManager.getRoomFromUUID(roomId);

        for(int i = 0; i < 5; i++){
            roomManager.processUserJoinRoomRequest(users[i], roomId, false);
        }

        //actual
        roomManager.processUserLeaveRoomRequest(users[0], roomId);
        assertNotEquals(room.getHost().getuuid(), users[0].getuuid());

    }

}
