package me.dulce.gamesite.gamesite2.transportcontroller;

import static org.junit.jupiter.api.Assertions.*;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.*;
import me.dulce.gamesite.gamesite2.user.User;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@SpringBootTest
public class RestWebControllerTest {

    @Autowired
    private RestWebController webController;

    @Autowired
    private RoomManager roomManager;
    
    @Test
    public void requestAnyPathChild_actualResource() {
        //assign

        //actual
        ModelAndView actual = webController.requestAnyPathChild("./index.html");

        //assert
        assertEquals("./index.html", actual.getViewName());
    }

    @Test
    public void requestAnyPathChild_nonResource() {
        //assign

        //actual
        ModelAndView actual = webController.requestAnyPathChild("random");

        //assert
        assertEquals("/pages", actual.getViewName());
    }

    @Test
    public void postJoinRoom_userJoinSuccess() {
        //assign
        User user = User.createGuestUser();
        User fakeHost = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        Room roomObj = roomManager.getRoomFromUUID(roomId);
        RoomJoinRequest request = new RoomJoinRequest();
        request.user = user.toMessagableObject();
        request.asSpectator = false;
        request.roomId = roomId.toString();

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertTrue(actual.getBody().success);
        assertTrue(roomObj.getAllJoinedUsers().contains(user));
    }

    @Test
    public void postJoinRoom_userAlreadyInRoom_leaveOldRoom() {
        //assign
        User user = User.createGuestUser();
        User fakeHost = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        Room roomObj = roomManager.getRoomFromUUID(roomId);
        UUID oldRoomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        Room oldRoomObj = roomManager.getRoomFromUUID(oldRoomId);
        RoomJoinRequest request = new RoomJoinRequest();
        request.user = user.toMessagableObject();
        request.asSpectator = false;
        request.roomId = roomId.toString();

        roomManager.processUserJoinRoomRequest(user, oldRoomId, false);

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertTrue(actual.getBody().success);
        assertTrue(roomObj.getAllJoinedUsers().contains(user));
        assertFalse(oldRoomObj.getAllJoinedUsers().contains(user));
    }

    @Test
    public void postJoinRoom_nullResourceHandled_badRequestCode() {
        //assign

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(null);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
    }

    @Test
    public void postJoinRoom_nullUserHandled_badRequestCode() {
        //assign
        RoomJoinRequest request = new RoomJoinRequest();
        request.roomId = UUID.randomUUID().toString();

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
    }

    @Test
    public void postJoinRoom_nullRoomIdHandled_badRequestCode() {
        //assign
        RoomJoinRequest request = new RoomJoinRequest();
        User userObj = User.createGuestUser();
        request.user = userObj.toMessagableObject();

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
    }

    @Test
    public void postJoinRoom_badFormatUUIDRoomId_badRequestCode() {
        //assign
        RoomJoinRequest request = new RoomJoinRequest();
        request.roomId = "Bad UUID";
        User userObj = User.createGuestUser();
        request.user = userObj.toMessagableObject();

        //actual
        ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
    }

    @Test
    public void postLeaveRoom_userLeaveSuccess() {
        //assign
        User user = User.createGuestUser();
        User fakeHost = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        Room roomObj = roomManager.getRoomFromUUID(roomId);
        roomManager.processUserJoinRoomRequest(user, roomId, false);
        RoomLeaveRequest request = new RoomLeaveRequest();
        request.user = user.toMessagableObject();
        request.roomId = roomId.toString();

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertFalse(roomObj.getAllJoinedUsers().contains(user));
    }

    @Test
    public void postLeaveRoom_userNotInRoomLeaveHandled() {
        //assign
        User user = User.createGuestUser();
        User fakeHost = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        Room roomObj = roomManager.getRoomFromUUID(roomId);
        RoomLeaveRequest request = new RoomLeaveRequest();
        request.user = user.toMessagableObject();
        request.roomId = roomId.toString();

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertFalse(roomObj.getAllJoinedUsers().contains(user));
    }

    @Test
    public void postLeaveRoom_nullUserFailed() {
        //assign
        User fakeHost = User.createGuestUser();
        UUID roomId = roomManager.createRoom(GameType.TEST, fakeHost, 10);
        RoomLeaveRequest request = new RoomLeaveRequest();
        request.user = null;
        request.roomId = roomId.toString();

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void postLeaveRoom_nullRequestHandled() {
        //assign

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(null);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void postLeaveRoom_nullRoomIdHandled() {
        //assign
        User user = User.createGuestUser();
        RoomLeaveRequest request = new RoomLeaveRequest();
        request.user = user.toMessagableObject();
        request.roomId = null;

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void postLeaveRoom_nullUserHandled() {
        //assign
        RoomLeaveRequest request = new RoomLeaveRequest();
        request.user = null;
        request.roomId = UUID.randomUUID().toString();

        //actual
        ResponseEntity<Object> actual = webController.postLeaveRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void postCreateRoom_roomCreateSuccess() {
        //assign
        User user = User.createGuestUser();
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = user.toMessagableObject();
        request.gameType = -2;
        request.maxLobbySize = 10;
        int expectedRoomNumber = roomManager.getAllRoomListings().length + 1;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertTrue(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
        assertEquals(roomManager.getRoomThatContainsUser(user).toString(), actual.getBody().roomId);
    }

    @Test
    public void postCreateRoom_badRoomCreation_okResponseNotSuccess() {
        //assign
        User user = User.createGuestUser();
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = user.toMessagableObject();
        request.gameType = -1;
        request.maxLobbySize = 10;
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }

    @Test
    public void postCreateRoom_badGameType_failCreation() {
        //assign
        User user = User.createGuestUser();
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = user.toMessagableObject();
        request.gameType = -100;
        request.maxLobbySize = 10;
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }

    @Test
    public void postCreateRoom_badUid_failCreation() {
        //assign
        User user = User.createGuestUser();
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = user.toMessagableObject();
        request.user.uuid = "bad uid";
        request.gameType = -2;
        request.maxLobbySize = 10;
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }

    @Test
    public void postCreateRoom_nullUser_failCreation() {
        //assign
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = null;
        request.gameType = -2;
        request.maxLobbySize = 10;
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }

    @Test
    public void postCreateRoom_LobbySizeLessThan0_failCreation() {
        //assign
        User user = User.createGuestUser();
        RoomCreateRequest request = new RoomCreateRequest();
        request.user = user.toMessagableObject();
        request.gameType = -2;
        request.maxLobbySize = -1;
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(request);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }

    @Test
    public void postCreateRoom_nullRequest_failCreation() {
        //assign
        int expectedRoomNumber = roomManager.getAllRoomListings().length;

        //actual
        ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(null);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertTrue(actual.hasBody());
        assertFalse(actual.getBody().success);
        assertEquals(expectedRoomNumber, roomManager.getAllRoomListings().length);
    }
}
