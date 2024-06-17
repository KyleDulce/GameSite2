package me.dulce.gamesite.rooms;

import me.dulce.commongames.CommonGamesReference;
import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameResolver;
import me.dulce.commongames.gamemessage.InitialGameMessageHandler;
import me.dulce.game.testgame.TestGameServiceManager;
import me.dulce.gamesite.transportcontroller.services.SocketMessengerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@Import(CommonGamesReference.class)
@ExtendWith({MockitoExtension.class})
@ContextConfiguration(classes = {RoomManager.class})
public class RoomManagerTest {

    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static UUID user1UUID;
    private static String userName = "SomeName";
    private static String userSession = "SomeSession";

    @MockBean SocketMessengerService socketMessengerService;

    @Autowired GameResolver gameResolver;

    @Autowired InitialGameMessageHandler initialGameMessageHandler;

    @Autowired private RoomManager roomManager;

    @BeforeAll
    public static void beforeTests() {
        user1UUID = UUID.fromString(user1UUID_str);
    }

    @Test
    public void allUsersLeave_RoomDoesNotExist() {

        // assign
        User testUser = User.createNewUser(user1UUID, userName, userSession);
        UUID roomId = roomManager.createRoom(testUser, 5, "test", TestGameServiceManager.GAME_ID);

        // actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        // assert
        assertFalse(roomManager.doesRoomExist(roomId));
    }

    @Test
    public void lastUserLeaves_GetRoomThatContainsUserNull() {

        // assign
        User testUser = User.createNewUser(user1UUID, userName, userSession);
        UUID roomId = roomManager.createRoom(testUser, 5, "test", TestGameServiceManager.GAME_ID);

        // actual
        roomManager.processUserLeaveRoomRequest(testUser, roomId);

        // assert
        assertNull(roomManager.getRoomThatContainsUser(testUser));
    }

    @Test
    public void hostLeaves_NewHostChosen() {

        // assign
        User[] users = new User[5];
        for (int i = 0; i < 5; i++) {
            users[i] = User.createNewUser(UUID.randomUUID(), userName, userSession);
        }

        UUID roomId = roomManager.createRoom(users[0], 5, "test", TestGameServiceManager.GAME_ID);
        assertNotNull(roomId);

        Room room = roomManager.getRoomFromUUID(roomId);

        for (int i = 0; i < 5; i++) {
            roomManager.processUserJoinRoomRequest(users[i], roomId, false);
        }

        // actual
        roomManager.processUserLeaveRoomRequest(users[0], roomId);
        assertNotEquals(room.getHost().getUuid(), users[0].getUuid());
    }
}
