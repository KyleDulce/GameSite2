package me.dulce.gamesite.transportcontroller;

// import java.util.Optional;
// import java.util.UUID;
// import me.dulce.gamesite.rooms.RoomManager;
// import me.dulce.gamesite.rooms.games.common.testgame.TestMessageData;
// import me.dulce.gamesite.transportcontroller.messaging.GameDataUpdate;
// import me.dulce.gamesite.transportcontroller.services.CookieService;
// import me.dulce.gamesite.transportcontroller.services.SocketMessengerService;
// import me.dulce.gamesite.user.User;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.junit.jupiter.SpringExtension;

// @ExtendWith({MockitoExtension.class, SpringExtension.class})
// @ContextConfiguration(classes = {SocketController.class})
public class SocketControllerTest {
    //  private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    //  private static final String fakeRoomId = "6e6f8345-82ec-4c23-934f-aa798bf5c6de";
    //  private static final String fakeJwtToken = "fakeToken";
    //  private static final String fakeSessionToken = "fakeSession";
    //
    //  private static UUID user1UUID;
    //  private static UUID room1UUID;
    //
    //  private static int cookieBuffer = 3;
    //
    //  @MockBean private SocketMessengerService socketMessengerService;
    //
    //  @MockBean private RoomManager roomManager;
    //
    //  @MockBean private CookieService cookieService;
    //
    //  @Autowired public SocketController socketController;

    //  @BeforeAll
    //  public static void beforeTests() {
    //    user1UUID = UUID.fromString(user1UUID_str);
    //    room1UUID = UUID.fromString(fakeRoomId);
    //  }
    //
    //  @AfterEach
    //  public void afterEachTest() {
    //    User.getCachedUsers().clear();
    //  }
    //
    //  @Test
    //  public void game_success() {
    //    setupMockCookie();
    //    GameDataUpdate request = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(true);
    //    when(roomManager.isUserInRoom(any(), any())).thenReturn(true);
    //    when(roomManager.handleIncomingRoomData(any(), any())).thenReturn(true);
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, never())
    //        .sendInvalidSocketMessageToUser(anyString(), any(), anyInt(), anyString());
    //    verify(roomManager, times(1)).handleIncomingRoomData(any(), any());
    //  }
    //
    //  @Test
    //  public void game_unauthorizedUser_authorized() {
    //    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.empty());
    //    GameDataUpdate request = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(true);
    //    when(roomManager.isUserInRoom(any(), any())).thenReturn(true);
    //    when(roomManager.handleIncomingRoomData(any(), any())).thenReturn(true);
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, times(1))
    //        .sendInvalidSocketMessageToUser(
    //            anyString(), any(), eq(HttpStatus.UNAUTHORIZED.value()), anyString());
    //    verify(roomManager, never()).handleIncomingRoomData(any(), any());
    //  }
    //
    //  @Test
    //  public void game_differentSocketSession_changesWithSuccess() {
    //    User user = User.createNewUser(user1UUID, cookieBuffer);
    //    user.setSocketId("differentSession");
    //    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.of(user));
    //    GameDataUpdate update = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(true);
    //    when(roomManager.isUserInRoom(any(), any())).thenReturn(true);
    //    when(roomManager.handleIncomingRoomData(any(), any())).thenReturn(true);
    //
    //    socketController.game(update, fakeSessionToken);
    //
    //    verify(socketMessengerService, never())
    //        .sendInvalidSocketMessageToUser(anyString(), any(), anyInt(), anyString());
    //    verify(roomManager, times(1)).handleIncomingRoomData(any(), any());
    //    assertEquals(fakeSessionToken, user.getSocketId());
    //  }
    //
    //  @Test
    //  public void game_invalidData_badRequest() {
    //    setupMockCookie();
    //    GameDataUpdate request = new GameDataUpdate();
    //    request.authToken = fakeJwtToken;
    //    request.gameData = null;
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, times(1))
    //        .sendInvalidSocketMessageToUser(
    //            anyString(), any(), eq(HttpStatus.BAD_REQUEST.value()), anyString());
    //  }
    //
    //  @Test
    //  public void game_invalidRoom_notFound() {
    //    setupMockCookie();
    //    GameDataUpdate request = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(false);
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, times(1))
    //        .sendInvalidSocketMessageToUser(
    //            anyString(), any(), eq(HttpStatus.NOT_FOUND.value()), anyString());
    //  }
    //
    //  @Test
    //  public void game_userNotInRoom_unauthorized() {
    //    setupMockCookie();
    //    GameDataUpdate request = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(true);
    //    when(roomManager.isUserInRoom(any(), any())).thenReturn(false);
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, times(1))
    //        .sendInvalidSocketMessageToUser(
    //            anyString(), any(), eq(HttpStatus.UNAUTHORIZED.value()), anyString());
    //    verify(roomManager, never()).handleIncomingRoomData(any(), any());
    //  }
    //
    //  @Test
    //  public void game_unsuccessfulHandling_returnedOkRequest() {
    //    setupMockCookie();
    //    GameDataUpdate request = getMockGameDataUpdate();
    //    when(roomManager.doesRoomExist(any())).thenReturn(true);
    //    when(roomManager.isUserInRoom(any(), any())).thenReturn(true);
    //    when(roomManager.handleIncomingRoomData(any(), any())).thenReturn(false);
    //
    //    socketController.game(request, fakeSessionToken);
    //
    //    verify(socketMessengerService, times(1))
    //        .sendInvalidSocketMessageToUser(anyString(), any(), eq(HttpStatus.OK.value()),
    // anyString());
    //  }
    //
    //  private void setupMockCookie() {
    //    User user = User.createNewUser(user1UUID, cookieBuffer);
    //    user.setSocketId(fakeSessionToken);
    //    when(cookieService.validateUserCookie(fakeJwtToken)).thenReturn(Optional.of(user));
    //  }
    //
    //  private GameDataUpdate getMockGameDataUpdate() {
    //    TestMessageData testMessageData = new TestMessageData();
    //    testMessageData.roomId = room1UUID;
    //    GameDataUpdate request = new GameDataUpdate();
    //    request.authToken = fakeJwtToken;
    //    request.gameData = testMessageData.parseObjectToDataMessage();
    //    return request;
    //  }
}
