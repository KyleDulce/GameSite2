package me.dulce.gamesite.gamesite2.rooms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.UUID;
import me.dulce.gamesite.gamesite2.rooms.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.rooms.games.common.BlankGameData;
import me.dulce.gamesite.gamesite2.rooms.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.rooms.games.common.settings.KickPlayerData;
import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestGame;
import me.dulce.gamesite.gamesite2.rooms.games.common.testgame.TestMessageData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameData;
import me.dulce.gamesite.gamesite2.rooms.games.generic.GameDataType;
import me.dulce.gamesite.gamesite2.transportcontroller.services.SocketMessengerService;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoomTest {

  private static final String roomUUID_str = "5eb7a634-22a1-43c0-9d35-8b0be08b7557";
  private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
  private static final String user2UUID_str = "7095790b-7a45-462c-8fbd-9506ec6a727a";
  private static final String user3UUID_str = "15b98a25-ba8c-44f0-a5fd-0e7788a1738e";
  private static final String hostUserUUID_str = "3fac37e7-aff8-4a1f-879d-2e32eeb28ba6";

  private static UUID roomUUID;
  private static UUID user1UUID;
  private static UUID user2UUID;
  private static UUID user3UUID;
  private static UUID hostUserUUID;

  private static int cookieBuffer = 3;

  private SocketMessengerService socketMessengerServiceMock;

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
    socketMessengerServiceMock = mock(SocketMessengerService.class);
  }

  @AfterEach
  public void afterEachTest() {
    User.getCachedUsers().clear();
  }

  @Test
  public void userJoin_userAddedToRoomSuccessfully() {
    // assign
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);

    // actual
    boolean actual = room.userJoin(user);

    // assert
    assertEquals(1, room.getUsersJoinedList().size());
    assertTrue(actual);
  }

  @Test
  public void userJoin_userBlockedFromRoomWhenFull_resultsFalse() {
    // assign
    Room room = getTestRoom();
    User user1 = User.createNewUser(user1UUID, cookieBuffer);
    User user2 = User.createNewUser(user2UUID, cookieBuffer);
    User user3 = User.createNewUser(user3UUID, cookieBuffer);
    room.userJoin(user1);
    room.userJoin(user2);

    // actual
    boolean actual = room.userJoin(user3);

    // assert
    assertEquals(2, room.getUsersJoinedList().size());
    assertFalse(actual);
    verify(socketMessengerServiceMock, never()).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void userJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
    // assign
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    room.userJoin(user);

    // actual
    boolean actual = room.userJoin(user);

    // assert
    assertEquals(1, room.getUsersJoinedList().size());
    assertFalse(actual);
    verify(socketMessengerServiceMock, never()).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void userJoin_userBlockedFromRoomWhenInProgress_resultsFalse() {
    // assign
    class RoomChild extends Room {
      RoomChild() {
        super(roomUUID, 2, null, "test", null);
        isInProgress = true;
      }

      @Override
      public GameType getGameType() {
        return null;
      }

      @Override
      protected boolean processGameDataForGame(User user, GameData response) {
        return false;
      }

      @Override
      protected void onUserJoinEvent(User user) {}

      @Override
      protected void onSpectatorJoinEvent(User user) {}

      @Override
      protected void onUserLeaveEvent(User user) {}
    }

    Room room = new RoomChild();
    User user = User.createNewUser(user1UUID, cookieBuffer);

    // actual
    boolean actual = room.userJoin(user);

    // assert
    assertEquals(0, room.getUsersJoinedList().size());
    assertFalse(actual);
    verify(socketMessengerServiceMock, never()).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void spectatorJoin_userAddedToRoomSuccessfully() {
    // assign
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);

    // actual
    boolean actual = room.spectatorJoin(user);

    // assert
    assertEquals(1, room.getSpectatorsJoinedList().size());
    assertTrue(actual);
  }

  @Test
  public void spectatorJoin_userBlockedFromRoomWhenDuplicateUser_resultsFalse() {
    // assign
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);
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
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    room.userJoin(user);

    // actual
    room.userLeave(user);

    // assert
    assertEquals(0, room.getSpectatorsJoinedList().size());
  }

  @Test
  public void userLeave_userRemovedFromRoomIfSpectating() {
    // assign
    Room room = getTestRoom();
    User user = User.createNewUser(user1UUID, cookieBuffer);
    room.spectatorJoin(user);

    // actual
    room.userLeave(user);

    // assert
    assertEquals(0, room.getSpectatorsJoinedList().size());
    verify(socketMessengerServiceMock, never()).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void userLeave_newHostWhenHostRemoved() {
    // assign
    Room room = getTestRoom();
    User host = User.createNewUser(hostUserUUID, cookieBuffer);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    room.userJoin(host);
    room.userJoin(user);

    // actual
    room.userLeave(host);

    // assert
    assertEquals(1, room.getUsersJoinedList().size());
    assertEquals(user, room.host);
    verify(socketMessengerServiceMock, times(1)).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void getRoomListingObject_RoomListingContainsSameData() {
    // assign
    User user = User.createNewUser(user1UUID, cookieBuffer);
    Room room =
        new Room(roomUUID, 2, user, "test", null) {
          @Override
          public GameType getGameType() {
            return GameType.NULL_GAME_TYPE;
          }

          @Override
          protected boolean processGameDataForGame(User user, GameData response) {
            return false;
          }

          @Override
          protected void onUserJoinEvent(User user) {}

          @Override
          protected void onSpectatorJoinEvent(User user) {}

          @Override
          protected void onUserLeaveEvent(User user) {}
        };
    String expectedUuid = room.getRoomId().toString();
    int expectedLobbySize = room.getUsersJoinedList().size();
    int expectedMaxLobbySize = room.getMaxUsers();
    int expectedSpectatorCount = room.getSpectatorsJoinedList().size();
    int expectedGameType = room.getGameType().getId();
    String expectedHostName = room.getHost().getName();
    boolean expectedProgressState = room.isInProgress();
    long expectedGameStartTime = room.getTimeStarted();

    // actual
    RoomListing actual = room.getRoomListingObject();

    // assert
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
  public void handleGameDataReceived_chatMessage_MessageIsBroadcast() {

    // assign
    SocketMessengerService messengerService = mock(SocketMessengerService.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    Room testRoom = new TestGame(roomUUID, 2, null, "test", messengerService);
    ChatMessageData sampleData =
        new ChatMessageData(testRoom.getRoomId(), "This is a message", "Bobby");

    // actual
    testRoom.handleGameDataReceived(user, sampleData);

    // assert
    verify(messengerService, times(1)).broadcastMessageToRoom(eq(testRoom), any());
  }

  @Test
  public void handleGameDataReceived_settingDataRequest_responseSent() {
    // assign
    SocketMessengerService messengerService = mock(SocketMessengerService.class);
    User user = User.createNewUser(hostUserUUID, cookieBuffer);
    user.setSocketId("fakeId");
    Room testRoom = new TestGame(roomUUID, 2, user, "test", messengerService);
    BlankGameData blankGameData = new BlankGameData(roomUUID, GameDataType.SETTINGS_DATA_REQUEST);

    // actual
    testRoom.handleGameDataReceived(user, blankGameData);

    // assert
    verify(messengerService, times(1)).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void handleGameDataReceived_settingDataRequestNotHost_sendError() {
    // assign
    SocketMessengerService messengerService = mock(SocketMessengerService.class);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    user.setSocketId("fakeId");
    Room testRoom = new TestGame(roomUUID, 2, null, "test", messengerService);
    BlankGameData blankGameData = new BlankGameData(roomUUID, GameDataType.SETTINGS_DATA_REQUEST);

    // actual
    testRoom.handleGameDataReceived(user, blankGameData);

    // assert
    verify(messengerService, times(1))
        .sendInvalidSocketMessageToUser(any(User.class), any(), anyInt(), anyString());
  }

  @Test
  public void handleGameDataReceived_kickPlayerRequest_responseSent() {
    // assign
    SocketMessengerService messengerService = mock(SocketMessengerService.class);
    User host = User.createNewUser(hostUserUUID, cookieBuffer);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    host.setSocketId("fakeId");
    user.setSocketId("fakeId");
    Room testRoom = new TestGame(roomUUID, 2, host, "test", messengerService);
    testRoom.userJoin(host);
    testRoom.userJoin(user);
    KickPlayerData kickPlayerData = new KickPlayerData(roomUUID, user);

    // actual
    testRoom.handleGameDataReceived(host, kickPlayerData);

    // assert
    assertEquals(1, testRoom.getUsersJoinedList().size());
    verify(messengerService, atLeastOnce()).sendMessageToUser(any(User.class), any(), any());
  }

  @Test
  public void handleGameDataReceived_kickPlayerRequestNotHost_sendError() {
    // assign
    SocketMessengerService messengerService = mock(SocketMessengerService.class);
    User host = User.createNewUser(hostUserUUID, cookieBuffer);
    User user = User.createNewUser(user1UUID, cookieBuffer);
    host.setSocketId("fakeId");
    user.setSocketId("fakeId");
    Room testRoom = new TestGame(roomUUID, 2, null, "test", messengerService);
    KickPlayerData kickPlayerData = new KickPlayerData(roomUUID, user);

    // actual
    testRoom.handleGameDataReceived(user, kickPlayerData);

    // assert
    verify(messengerService, times(1))
        .sendInvalidSocketMessageToUser(any(User.class), any(), anyInt(), anyString());
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
    User hostUser = User.createNewUser(hostUserUUID, cookieBuffer);
    return new Room(roomUUID, 2, hostUser, "test", socketMessengerServiceMock) {
      @Override
      public GameType getGameType() {
        return null;
      }

      @Override
      protected boolean processGameDataForGame(User user, GameData response) {
        return false;
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
