package me.dulce.gamesite.gamesite2.rooms;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {RoomManager.class})
public class RoomManagerTest {

    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static UUID user1UUID;

    @MockBean
    SocketMessengerService socketMessengerService;

    @Autowired
    private RoomManager roomManager;

    @BeforeAll
    public static void beforeTests() {
        user1UUID = UUID.fromString(user1UUID_str);
    }

    @Test
    public void allUsersLeave_RoomDoesNotExist(){

        //assign
        User testUser = User.createNewUser(user1UUID);
        UUID roomId = roomManager.createRoom(GameType.TEST, testUser, 5, "test");

        //actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        //assert
        assertFalse(roomManager.doesRoomExist(roomId));

    }

    @Test
    public void lastUserLeaves_GetRoomThatContainsUserNull(){

        //assign
        User testUser = User.createNewUser(user1UUID);
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
            users[i] = User.createNewUser(UUID.randomUUID());
        }

        UUID roomId = roomManager.createRoom(GameType.TEST, users[0], 5, "test");
        Room room = roomManager.getRoomFromUUID(roomId);

        for(int i = 0; i < 5; i++){
            roomManager.processUserJoinRoomRequest(users[i], roomId, false);
        }

        //actual
        roomManager.processUserLeaveRoomRequest(users[0], roomId);
        assertNotEquals(room.getHost().getUuid(), users[0].getUuid());

    }

}
