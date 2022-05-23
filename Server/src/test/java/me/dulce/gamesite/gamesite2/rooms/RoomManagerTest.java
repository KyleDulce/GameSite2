package me.dulce.gamesite.gamesite2.rooms;

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
        UUID roomId = roomManager.createRoom(GameType.TEST, testUser, 5);

        //actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        //assert
        assertFalse(roomManager.doesRoomExist(roomId));

    }

}
