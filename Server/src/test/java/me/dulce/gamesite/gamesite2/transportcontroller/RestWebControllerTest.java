package me.dulce.gamesite.gamesite2.transportcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.*;
import me.dulce.gamesite.gamesite2.transportcontroller.services.AuthService;
import me.dulce.gamesite.gamesite2.transportcontroller.services.CookieService;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {RestWebController.class})
public class RestWebControllerTest {

  private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
  private static final String user2UUID_str = "7095790b-7a45-462c-8fbd-9506ec6a727a";
  private static final String fakeJwtToken = "fakeToken";
  private static final String fakeRoomId = "6e6f8345-82ec-4c23-934f-aa798bf5c6de";
  private static final String fakeRoomId2 = "9812340d-36ff-4f83-8e0b-a9e3f4728aed";

  private static UUID user1UUID;
  private static UUID user2UUID;

  private static int cookieBuffer = 3;

  @MockBean private RoomManager roomManager;

  @MockBean private AppConfig config;

  @MockBean private AuthService authService;

  @MockBean private CookieService cookieService;

  @Autowired private RestWebController webController;

  @BeforeAll
  public static void beforeTests() {
    user1UUID = UUID.fromString(user1UUID_str);
    user2UUID = UUID.fromString(user2UUID_str);
  }

  @AfterEach
  public void afterEachTest() {
    User.getCachedUsers().clear();
  }

  @Test
  public void getRoomLists_getsRooms() {
    setupFullMockCookie();
    Room.RoomListing listing =
        Room.RoomListing.builder()
            .roomId("id1")
            .lobbySize(1)
            .maxLobbySize(5)
            .spectatorsAmount(0)
            .gameType(-1)
            .hostName("name")
            .inProgress(false)
            .gameStartTime(0)
            .roomName("name")
            .build();
    when(roomManager.getAllRoomListings()).thenReturn(new Room.RoomListing[] {listing});

    ResponseEntity<Room.RoomListing[]> actual = webController.getRoomLists(fakeJwtToken);

    assertEquals(1, actual.getBody().length);
    assertEquals(listing, actual.getBody()[0]);
  }

  @Test
  public void getRoomLists_unauthorizedUser_unauthorized() {
    when(cookieService.validateUserCookie(anyString())).thenReturn(Optional.empty());

    ResponseEntity<Room.RoomListing[]> actual = webController.getRoomLists(fakeJwtToken);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
  }

  @Test
  public void postJoinRoom_userJoinSuccess() {
    // assign
    setupFullMockCookie();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = false;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(null);
    when(roomManager.processUserJoinRoomRequest(any(), eq(UUID.fromString(fakeRoomId)), eq(false)))
        .thenReturn(true);

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, times(1))
        .processUserJoinRoomRequest(any(), eq(UUID.fromString(fakeRoomId)), eq(false));
  }

  @Test
  public void postJoinRoom_userAlreadyInRoom_leaveOldRoom() {
    // assign
    setupFullMockCookie();
    UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    UUID fakeRoom2UUID = UUID.fromString(fakeRoomId2);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = false;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom2UUID);
    when(roomManager.processUserJoinRoomRequest(eq(user), eq(fakeRoom1UUID), anyBoolean()))
        .thenReturn(true);

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(user), eq(fakeRoom2UUID));
  }

  @Test
  public void postJoinRoom_userAlreadyInSameRoomAsSpec_doNothingSuccess() {
    // assign
    setupFullMockCookie();
    UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    Room fakeRoom1 = mock(Room.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = true;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
    when(roomManager.processUserJoinRoomRequest(eq(user), eq(fakeRoom1UUID), anyBoolean()))
        .thenReturn(true);
    when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
    when(fakeRoom1.getUsersJoinedList()).thenReturn(Collections.emptyList());

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, never()).processUserLeaveRoomRequest(eq(user), eq(fakeRoom1UUID));
  }

  @Test
  public void postJoinRoom_userAlreadyInSameRoomAsJoined_doNothingSuccess() {
    // assign
    setupFullMockCookie();
    UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    Room fakeRoom1 = mock(Room.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = false;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
    when(roomManager.processUserJoinRoomRequest(eq(user), eq(fakeRoom1UUID), anyBoolean()))
        .thenReturn(true);
    when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
    when(fakeRoom1.getSpectatorsJoinedList()).thenReturn(Collections.emptyList());

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, never()).processUserLeaveRoomRequest(eq(user), eq(fakeRoom1UUID));
  }

  @Test
  public void postJoinRoom_userRequestSpecAlreadyJoined_leaveRejoinSuccess() {
    // assign
    setupFullMockCookie();
    UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    Room fakeRoom1 = mock(Room.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = false;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
    when(roomManager.processUserJoinRoomRequest(eq(user), eq(fakeRoom1UUID), anyBoolean()))
        .thenReturn(true);
    when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
    when(fakeRoom1.getSpectatorsJoinedList()).thenReturn(List.of(user));

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(user), eq(fakeRoom1UUID));
  }

  @Test
  public void postJoinRoom_userRequestJoinAlreadySpec_leaveRejoinSuccess() {
    // assign
    setupFullMockCookie();
    UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    Room fakeRoom1 = mock(Room.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomJoinRequest request = new RoomJoinRequest();
    request.asSpectator = true;
    request.roomId = fakeRoomId;
    when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
    when(roomManager.processUserJoinRoomRequest(eq(user), eq(fakeRoom1UUID), anyBoolean()))
        .thenReturn(true);
    when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
    when(fakeRoom1.getUsersJoinedList()).thenReturn(List.of(user));

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(user), eq(fakeRoom1UUID));
  }

  @Test
  public void postJoinRoom_nullResourceHandled_badRequestCode() {
    // assign
    setupMockCookie();

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, null);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void postJoinRoom_nullRoomIdHandled_badRequestCode() {
    // assign
    setupMockCookie();
    RoomJoinRequest request = new RoomJoinRequest();
    User userObj = User.createNewUser(user1UUID, cookieBuffer);

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void postJoinRoom_badFormatUUIDRoomId_badRequestCode() {
    // assign
    setupMockCookie();
    RoomJoinRequest request = new RoomJoinRequest();
    request.roomId = "Bad UUID";
    User userObj = User.createNewUser(user1UUID, cookieBuffer);

    // actual
    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void postJoinRoom_unauthenticated_unauthorized() {
    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.empty());
    RoomJoinRequest request = new RoomJoinRequest();
    request.roomId = "Bad UUID";
    User userObj = User.createNewUser(user1UUID, cookieBuffer);

    ResponseEntity<RoomJoinResponse> actual = webController.postJoinRoom(fakeJwtToken, request);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    verify(roomManager, never()).processUserJoinRoomRequest(any(), any(), anyBoolean());
  }

  @Test
  public void postLeaveRoom_userLeaveSuccess() {
    // assign
    setupFullMockCookie();
    UUID roomId = UUID.fromString(fakeRoomId);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomLeaveRequest request = new RoomLeaveRequest();
    request.roomId = fakeRoomId;

    // actual
    ResponseEntity<Object> actual = webController.postLeaveRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(user), eq(roomId));
  }

  @Test
  public void postLeaveRoom_unAuthenticatedUser_unauthorized() {
    // assign
    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.empty());
    RoomLeaveRequest request = new RoomLeaveRequest();
    request.roomId = fakeRoomId;

    // actual
    ResponseEntity<Object> actual = webController.postLeaveRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    verify(roomManager, never()).processUserLeaveRoomRequest(any(), any());
  }

  @Test
  public void postLeaveRoom_nullRequestHandled() {
    // assign
    setupMockCookie();

    // actual
    ResponseEntity<Object> actual = webController.postLeaveRoom(fakeJwtToken, null);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
  }

  @Test
  public void postLeaveRoom_nullRoomIdHandled() {
    // assign
    setupMockCookie();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomLeaveRequest request = new RoomLeaveRequest();
    request.roomId = null;

    // actual
    ResponseEntity<Object> actual = webController.postLeaveRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
  }

  @Test
  public void postCreateRoom_roomCreateSuccess() {
    // assign
    setupFullMockCookie();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    RoomCreateRequest request = new RoomCreateRequest();
    request.gameType = GameType.TEST.getId();
    request.maxLobbySize = 10;
    when(roomManager.createRoom(eq(GameType.TEST), eq(user), eq(10), anyString()))
        .thenReturn(UUID.fromString(fakeRoomId));

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertTrue(actual.getBody().success);
    assertEquals(fakeRoomId, actual.getBody().roomId);
    verify(roomManager, times(1)).createRoom(eq(GameType.TEST), eq(user), eq(10), anyString());
  }

  @Test
  public void postCreateRoom_badRoomCreation_okResponseNotSuccess() {
    // assign
    setupFullMockCookie();
    RoomCreateRequest request = new RoomCreateRequest();
    request.gameType = -1;
    request.maxLobbySize = 10;
    when(roomManager.createRoom(any(), any(), anyInt(), anyString())).thenReturn(null);

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
    verify(roomManager, times(1))
        .createRoom(eq(GameType.NULL_GAME_TYPE), any(), eq(10), anyString());
  }

  @Test
  public void postCreateRoom_badGameType_failCreation() {
    // assign
    setupMockCookie();
    RoomCreateRequest request = new RoomCreateRequest();
    request.gameType = -100;
    request.maxLobbySize = 10;

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void postCreateRoom_unAuthorized_unauthorized() {
    // assign
    when(cookieService.validateUserCookie(anyString())).thenReturn(Optional.empty());
    RoomCreateRequest request = new RoomCreateRequest();
    request.gameType = -2;
    request.maxLobbySize = 10;

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
    verify(roomManager, never()).createRoom(any(), any(), anyInt(), anyString());
  }

  @Test
  public void postCreateRoom_LobbySizeLessThan0_failCreation() {
    // assign
    setupMockCookie();
    RoomCreateRequest request = new RoomCreateRequest();
    request.gameType = -2;
    request.maxLobbySize = -1;

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, request);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void postCreateRoom_nullRequest_failCreation() {
    // assign
    setupMockCookie();

    // actual
    ResponseEntity<RoomCreateResponse> actual = webController.postCreateRoom(fakeJwtToken, null);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertTrue(actual.hasBody());
    assertFalse(actual.getBody().success);
  }

  @Test
  public void getRoomInfo_success() {
    setupFullMockCookie();
    Room mockRoom = mock(Room.class);
    Room.RoomListing mockRoomListing = mock(Room.RoomListing.class);
    User mockUser = mock(User.class);
    when(roomManager.getRoomFromUUID(any())).thenReturn(mockRoom);
    when(mockRoom.getRoomListingObject()).thenReturn(mockRoomListing);
    when(mockRoom.getHost()).thenReturn(mockUser);
    when(mockUser.equals(any())).thenReturn(true);

    ResponseEntity<RoomInfoResponse> actual = webController.getRoomInfo(fakeJwtToken, fakeRoomId);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(mockRoomListing, actual.getBody().room);
    assertTrue(actual.getBody().isHost);
  }

  @Test
  public void getRoomInfo_unauthenticatedUser_unAuthorized() {
    setupMockCookie();
    when(cookieService.validateUserCookie(anyString())).thenReturn(Optional.empty());

    ResponseEntity<RoomInfoResponse> actual = webController.getRoomInfo(fakeJwtToken, fakeRoomId);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    assertFalse(actual.hasBody());
  }

  @Test
  public void getRoomInfo_invalidUUID_badRequest() {
    setupMockCookie();

    ResponseEntity<RoomInfoResponse> actual = webController.getRoomInfo(fakeJwtToken, "Bad uid");

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertFalse(actual.hasBody());
  }

  @Test
  public void getRoomInfo_invalidRoom_badRequest() {
    setupMockCookie();
    when(roomManager.getRoomFromUUID(any())).thenReturn(null);

    ResponseEntity<RoomInfoResponse> actual = webController.getRoomInfo(fakeJwtToken, fakeRoomId);

    assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    assertFalse(actual.hasBody());
  }

  @Test
  public void postUpdateUser_success() {
    String expectedName = "A specific Name";
    User user = User.createNewUser(user1UUID, cookieBuffer);
    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.of(user));
    setupMockCookieResponse("");
    UserUpdateRequest request = new UserUpdateRequest();
    request.name = expectedName;

    ResponseEntity<UserUpdateResponse> actual = webController.postUpdateUser(fakeJwtToken, request);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.getBody().success);
    assertEquals(expectedName, user.getName());
  }

  @Test
  public void postUpdateUser_blankName_failedRequest() {
    String requestedName = "   ";
    String expectedName = "This is a name";
    User user = User.createNewUser(user1UUID, expectedName, cookieBuffer);
    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.of(user));
    UserUpdateRequest request = new UserUpdateRequest();
    request.name = requestedName;

    ResponseEntity<UserUpdateResponse> actual = webController.postUpdateUser(fakeJwtToken, request);

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertFalse(actual.getBody().success);
    assertEquals(expectedName, user.getName());
  }

  @Test
  public void postUpdateUser_unauthorized_unauthorizedResponse() {
    String requestedName = "This is a name";
    String expectedName = "This is another name";
    User user = User.createNewUser(user1UUID, expectedName, cookieBuffer);
    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.empty());
    UserUpdateRequest request = new UserUpdateRequest();
    request.name = requestedName;

    ResponseEntity<UserUpdateResponse> actual = webController.postUpdateUser(fakeJwtToken, request);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    assertFalse(actual.getBody().success);
    assertEquals(expectedName, user.getName());
  }

  @Test
  public void getRefreshToken_success() {
    String expectedCookieValue = "Some value";
    setupMockCookie();
    setupMockCookieResponse(expectedCookieValue);

    ResponseEntity<?> actual = webController.getRefreshToken(fakeJwtToken);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(expectedCookieValue, actual.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
  }

  @Test
  public void getRefreshToken_unauthorized_unauthorizedResponse() {
    when(cookieService.validateUserCookie(anyString())).thenReturn(Optional.empty());

    ResponseEntity<?> actual = webController.getRefreshToken(fakeJwtToken);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    verify(cookieService, never()).getUserCookie(any());
  }

  @Test
  public void postAuth_noPreviousCookie_success() {
    when(cookieService.getNameFromCookie(anyString())).thenReturn(Optional.empty());
    when(config.getCookieIdBuffer()).thenReturn(3);
    String samplePassword = "SomePass";
    String sampleLogin = "SomeLogin";
    UserAuthRequest request = new UserAuthRequest();
    request.login = sampleLogin;
    request.passHash = samplePassword;
    when(authService.validateAuthCreds(eq(sampleLogin), eq(samplePassword)))
        .thenReturn(Optional.of(user1UUID));
    setupMockCookieResponse(fakeJwtToken);

    ResponseEntity<UserAuthResponse> actual = webController.postAuth("bad-token", request);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.getBody().success);
    assertEquals(fakeJwtToken, actual.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
    assertEquals(1, User.getCachedUsers().size());

    User.clearCache();
  }

  @Test
  public void postAuth_hasPreviousCookie_successAndChangeName() {
    String expectedName = "SomeName";
    when(cookieService.getNameFromCookie(anyString())).thenReturn(Optional.of(expectedName));
    when(config.getCookieIdBuffer()).thenReturn(3);
    String samplePassword = "SomePass";
    String sampleLogin = "SomeLogin";
    UserAuthRequest request = new UserAuthRequest();
    request.login = sampleLogin;
    request.passHash = samplePassword;
    when(authService.validateAuthCreds(eq(sampleLogin), eq(samplePassword)))
        .thenReturn(Optional.of(user1UUID));
    setupMockCookieResponse(fakeJwtToken);

    ResponseEntity<UserAuthResponse> actual = webController.postAuth("good-token", request);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertTrue(actual.getBody().success);
    assertEquals(fakeJwtToken, actual.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
    assertEquals(1, User.getCachedUsers().size());
    assertEquals(expectedName, User.getCachedUsers().get(user1UUID).getName());

    User.clearCache();
  }

  @Test
  public void postAuth_nullRequest_badRequest() {

    ResponseEntity<UserAuthResponse> actual = webController.postAuth("bad-token", null);

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    assertFalse(actual.getBody().success);
    assertEquals(0, User.getCachedUsers().size());

    User.clearCache();
  }

  @Test
  public void postAuth_badCreds_unauthorized() {
    when(cookieService.getNameFromCookie(anyString())).thenReturn(Optional.empty());
    String samplePassword = "SomePass";
    String sampleLogin = "SomeLogin";
    UserAuthRequest request = new UserAuthRequest();
    request.login = sampleLogin;
    request.passHash = samplePassword;
    when(authService.validateAuthCreds(eq(sampleLogin), eq(samplePassword)))
        .thenReturn(Optional.empty());

    ResponseEntity<UserAuthResponse> actual = webController.postAuth("bad-token", request);

    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    assertFalse(actual.getBody().success);
    assertEquals(0, User.getCachedUsers().size());

    User.clearCache();
  }

  @Test
  public void deleteAuthenticationToken_success() {
    String responseCookie = "deleteCookie";
    when(cookieService.validateUserCookie(eq(fakeJwtToken)))
        .thenReturn(Optional.of(User.createNewUser(user1UUID, cookieBuffer)));
    ResponseCookie responseCookieMock = mock(ResponseCookie.class);
    when(responseCookieMock.toString()).thenReturn(responseCookie);
    when(cookieService.getDeleteCookie(any())).thenReturn(responseCookieMock);

    ResponseEntity<?> actual = webController.deleteAuthenticationToken(fakeJwtToken);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(responseCookie, actual.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
  }

  @Test
  public void deleteAuthenticationToken_noCookie_badRequest() {
    when(cookieService.validateUserCookie(eq(fakeJwtToken))).thenReturn(Optional.empty());

    ResponseEntity<?> actual = webController.deleteAuthenticationToken(fakeJwtToken);

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
  }

  private void setupMockCookie() {
    when(cookieService.validateUserCookie(fakeJwtToken))
        .thenReturn(Optional.of(User.createNewUser(user1UUID, cookieBuffer)));
  }

  private void setupMockCookieResponse(String cookieValue) {
    ResponseCookie responseCookieMock = mock(ResponseCookie.class);
    when(responseCookieMock.toString()).thenReturn(cookieValue);
    when(cookieService.getUserCookie(any())).thenReturn(responseCookieMock);
  }

  private void setupFullMockCookie() {
    setupMockCookie();
    setupMockCookieResponse("");
  }
}
