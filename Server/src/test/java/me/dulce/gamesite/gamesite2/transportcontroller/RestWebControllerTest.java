package me.dulce.gamesite.gamesite2.transportcontroller;

import static me.dulce.gamesite.gamesite2.testutils.TestUtils.*;
import static me.dulce.gamesite.gamesite2.testutils.SpringSecurityTestConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.security.JwtSecurityCookieService;
import me.dulce.gamesite.gamesite2.testutils.SpringSecurityTestConfiguration;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.*;
import me.dulce.gamesite.gamesite2.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(RestWebController.class)
@ContextConfiguration(classes = {SpringSecurityTestConfiguration.class})
public class RestWebControllerTest {

    private static final String ROOM_LIST_ENDPOINT = "/api/getRoomLists";
    private static final String ROOM_JOIN_ENDPOINT = "/api/joinRoom";
    private static final String ROOM_LEAVE_ENDPOINT = "/api/leaveRoom";
    private static final String ROOM_CREATE_ENDPOINT = "/api/createRoom";
    private static final String ROOM_INFO_ENDPOINT = "/api/roomInfo";
    private static final String TOKEN_REFRESH_ENDPOINT = "/api/refreshToken";
    private static final String AUTH_ENDPOINT = "/api/authenticate";
    private static final String INVALIDATE_AUTH_ENDPOINT = "/api/invalidateAuthentication";

    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static final String user2UUID_str = "7095790b-7a45-462c-8fbd-9506ec6a727a";
    private static final String fakeRoomId = "6e6f8345-82ec-4c23-934f-aa798bf5c6de";
    private static final String fakeRoomId2 = "9812340d-36ff-4f83-8e0b-a9e3f4728aed";

    private static final UUID user1UUID = UUID.fromString(user1UUID_str);
    private static final UUID user2UUID = UUID.fromString(user2UUID_str);
    private static final UUID fakeRoom1UUID = UUID.fromString(fakeRoomId);
    private static final UUID fakeRoom2UUID = UUID.fromString(fakeRoomId2);


    @MockBean
    private RoomManager roomManager;
    @MockBean
    private JwtSecurityCookieService jwtSecurityCookieService;

    @MockBean
    private AppConfig config;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    public void afterEachTest() {
        User.getCachedUsers().clear();
    }

    @Test
    @WithBasicUser
    public void getRoomLists_getsRooms() throws Exception {
        Room.RoomListing listing =
                Room.RoomListing.builder()
                        .roomId("id1")
                        .lobbySize(1)
                        .maxLobbySize(5)
                        .spectatorsAmount(0)
                        .gameType("TESTING")
                        .hostName("name")
                        .inProgress(false)
                        .gameStartTime(0)
                        .roomName("name")
                        .build();
        when(roomManager.getAllRoomListings()).thenReturn(new Room.RoomListing[]{listing});

        getRequest(mockMvc, ROOM_LIST_ENDPOINT, HttpStatus.OK)
                .andExpect(content().json(objectAsArrayToString(listing)));
    }

    @Test
    @WithAnonymousUser
    public void getRoomLists_unauthorizedUser_unauthorized() throws Exception {
        getRequest(mockMvc, ROOM_LIST_ENDPOINT, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userJoinSuccess() throws Exception {

        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        when(roomManager.getRoomThatContainsUser(any())).thenReturn(null);
        when(roomManager.processUserJoinRoomRequest(any(), eq(UUID.fromString(fakeRoomId)), eq(false)))
                .thenReturn(true);

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, times(1))
                .processUserJoinRoomRequest(any(), eq(UUID.fromString(fakeRoomId)), eq(false));
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userAlreadyInRoom_leaveOldRoom() throws Exception {
        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
        when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom2UUID);
        when(roomManager.processUserJoinRoomRequest(eq(basicUser), eq(fakeRoom1UUID), anyBoolean()))
                .thenReturn(true);

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom2UUID));
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userAlreadyInSameRoomAsSpec_doNothingSuccess() throws Exception {
        User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        Room fakeRoom1 = mock(Room.class);
        when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
        when(roomManager.processUserJoinRoomRequest(eq(basicUser), eq(fakeRoom1UUID), anyBoolean()))
                .thenReturn(true);
        when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
        when(fakeRoom1.getUsersJoinedList()).thenReturn(Collections.emptyList());

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), getMVMapFromString("asSpectator=true"), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, never()).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom1UUID));
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userAlreadyInSameRoomAsJoined_doNothingSuccess() throws Exception {
        User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
        Room fakeRoom1 = mock(Room.class);
        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
        when(roomManager.processUserJoinRoomRequest(eq(basicUser), eq(fakeRoom1UUID), anyBoolean()))
                .thenReturn(true);
        when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
        when(fakeRoom1.getSpectatorsJoinedList()).thenReturn(Collections.emptyList());

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, never()).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom1UUID));
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userRequestSpecAlreadyJoined_leaveRejoinSuccess() throws Exception {
        User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
        Room fakeRoom1 = mock(Room.class);
        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
        when(roomManager.processUserJoinRoomRequest(eq(basicUser), eq(fakeRoom1UUID), anyBoolean()))
                .thenReturn(true);
        when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
        when(fakeRoom1.getSpectatorsJoinedList()).thenReturn(List.of(basicUser));

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom1UUID));
    }

    @Test
    @WithBasicUser
    public void postJoinRoom_userRequestJoinAlreadySpec_leaveRejoinSuccess() throws Exception {
        User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
        Room fakeRoom1 = mock(Room.class);
        RoomJoinResponse expectedResponse = new RoomJoinResponse(true, false);

        when(roomManager.getRoomThatContainsUser(any())).thenReturn(fakeRoom1UUID);
        when(roomManager.processUserJoinRoomRequest(eq(basicUser), eq(fakeRoom1UUID), anyBoolean()))
                .thenReturn(true);
        when(roomManager.getRoomFromUUID(any())).thenReturn(fakeRoom1);
        when(fakeRoom1.getUsersJoinedList()).thenReturn(List.of(basicUser));

        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), getMVMapFromString("asSpectator=true"), HttpStatus.OK)
                .andExpect(content().json(objectToString(expectedResponse)));

        verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom1UUID));
    }

      @Test
      @WithBasicUser
      public void postJoinRoom_nullResourceHandled_methodNoAllowed() throws Exception {
        postRequest(mockMvc, ROOM_JOIN_ENDPOINT, HttpStatus.METHOD_NOT_ALLOWED);
      }

      @Test
      @WithBasicUser
      public void postJoinRoom_badFormatUUIDRoomId_badRequestCode() throws Exception {
          postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, "Bad UUID"), HttpStatus.BAD_REQUEST);
      }

      @Test
      @WithAnonymousUser
      public void postJoinRoom_unauthenticated_unauthorized() throws Exception {
        postRequest(mockMvc, concatPaths(ROOM_JOIN_ENDPOINT, fakeRoomId), HttpStatus.UNAUTHORIZED);
        verify(roomManager, never()).processUserJoinRoomRequest(any(), any(), anyBoolean());
      }

      @Test
      @WithBasicUser
      public void postLeaveRoom_userLeaveSuccess() throws Exception {
          User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);

          postRequest(mockMvc, concatPaths(ROOM_LEAVE_ENDPOINT, fakeRoomId), HttpStatus.OK);

          verify(roomManager, times(1)).processUserLeaveRoomRequest(eq(basicUser), eq(fakeRoom1UUID));
      }

      @Test
      @WithAnonymousUser
      public void postLeaveRoom_unAuthenticatedUser_unauthorized() throws Exception {
          postRequest(mockMvc, concatPaths(ROOM_LEAVE_ENDPOINT, fakeRoomId), HttpStatus.UNAUTHORIZED);
          verify(roomManager, never()).processUserLeaveRoomRequest(any(), any());
      }

      @Test
      @WithBasicUser
      public void postLeaveRoom_nullRequestHandled() throws Exception {
          postRequest(mockMvc, ROOM_LEAVE_ENDPOINT, HttpStatus.METHOD_NOT_ALLOWED);
      }

      @Test
      @WithBasicUser
      public void postCreateRoom_roomCreateSuccess() throws Exception {
          RoomCreateResponse expectedResponse = new RoomCreateResponse(true, fakeRoomId);
          User basicUser = User.getUserFromSecurityDetails(BASIC_USER_DETAILS);
          when(roomManager.createRoom(eq(GameType.TEST), eq(basicUser), eq(10), anyString()))
                  .thenReturn(UUID.fromString(fakeRoomId));

          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=10", "gameType=TESTING"),
                  HttpStatus.OK)
                  .andExpect(content().json(objectToString(expectedResponse)));

          verify(roomManager, times(1)).createRoom(eq(GameType.TEST), eq(basicUser), eq(10),
                  anyString());
      }

      @Test
      @WithBasicUser
      public void postCreateRoom_badRoomCreation_okResponseNotSuccess() throws Exception {
          RoomCreateResponse expectedResponse = new RoomCreateResponse(false, null);
          when(roomManager.createRoom(any(), any(), anyInt(), anyString())).thenReturn(null);

          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=10", "gameType=TESTING"),
                  HttpStatus.OK)
                  .andExpect(content().json(objectToString(expectedResponse)));

          verify(roomManager, times(1))
                  .createRoom(eq(GameType.TEST), any(), eq(10), anyString());
      }

      @Test
      @WithBasicUser
      public void postCreateRoom_badGameType_failCreation() throws Exception {
          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=10", "gameType=BadGame"),
                  HttpStatus.BAD_REQUEST);
      }

      @Test
      @WithAnonymousUser
      public void postCreateRoom_unAuthorized_unauthorized() throws Exception {
          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=10", "gameType=TESTING"),
                  HttpStatus.UNAUTHORIZED);
        verify(roomManager, never()).createRoom(any(), any(), anyInt(), anyString());
      }

      @Test
      @WithBasicUser
      public void postCreateRoom_LobbySizeLessThan0_failCreation() throws Exception {
          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=-1", "gameType=TESTING"),
                  HttpStatus.BAD_REQUEST);
      }

      @Test
      @WithBasicUser
      public void postCreateRoom_nullRequest_failCreation() throws Exception {
          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("gameType=TESTING"),
                  HttpStatus.BAD_REQUEST);
          postRequest(mockMvc, ROOM_CREATE_ENDPOINT,
                  getMVMapFromString("maxLobbySize=10"),
                  HttpStatus.BAD_REQUEST);
      }

      @Test
      @WithBasicUser
      public void getRoomInfo_success() throws Exception {
          Room.RoomListing roomListing = new Room.RoomListing("id", 1, 2, 3, "TESTING", "host", true, 10, "name");
          RoomInfoResponse expectedResponse = new RoomInfoResponse(roomListing, true, false, false);

        Room mockRoom = mock(Room.class);
        User mockUser = mock(User.class);
        when(roomManager.getRoomFromUUID(any())).thenReturn(mockRoom);
        when(mockRoom.getRoomListingObject()).thenReturn(roomListing);
        when(mockRoom.getHost()).thenReturn(mockUser);
        when(mockUser.equals(any())).thenReturn(true);

          getRequest(mockMvc, concatPaths(ROOM_INFO_ENDPOINT, fakeRoomId), HttpStatus.OK)
                  .andExpect(content().json(objectToString(expectedResponse)));
      }

      @Test
      @WithAnonymousUser
      public void getRoomInfo_unauthenticatedUser_unAuthorized() throws Exception {
          getRequest(mockMvc, concatPaths(ROOM_INFO_ENDPOINT, fakeRoomId), HttpStatus.UNAUTHORIZED);
      }

      @Test
      @WithBasicUser
      public void getRoomInfo_invalidUUID_badRequest() throws Exception {
          getRequest(mockMvc, concatPaths(ROOM_INFO_ENDPOINT, "badUid"), HttpStatus.BAD_REQUEST);
      }

      @Test
      @WithBasicUser
      public void getRoomInfo_invalidRoom_badRequest() throws Exception {
        when(roomManager.getRoomFromUUID(any())).thenReturn(null);
          getRequest(mockMvc, concatPaths(ROOM_INFO_ENDPOINT, fakeRoomId), HttpStatus.NOT_FOUND);
      }

      @Test
      @WithBasicUser
      public void getRefreshToken_success() throws Exception {

          String expectedCookieValue = "sessionId=abc";
          ResponseCookie mockCookie = mock(ResponseCookie.class);
          when(mockCookie.toString()).thenReturn(expectedCookieValue);

        when(jwtSecurityCookieService.generateNewResponseCookie(any()))
                .thenReturn(mockCookie);

          getRequest(mockMvc, TOKEN_REFRESH_ENDPOINT, HttpStatus.OK)
                  .andExpect(cookie().exists("sessionId"));
      }

      @Test
      @WithAnonymousUser
      public void getRefreshToken_unauthorized_unauthorizedResponse() throws Exception {
          getRequest(mockMvc, TOKEN_REFRESH_ENDPOINT, HttpStatus.UNAUTHORIZED);
          verify(jwtSecurityCookieService, never()).generateNewResponseCookie(any());
      }

      @Test
      @WithAnonymousUser
      public void postAuth_nullRequest_unauthorized() throws Exception {
          postRequest(mockMvc, AUTH_ENDPOINT, HttpStatus.UNAUTHORIZED);

        assertEquals(0, User.getCachedUsers().size());
      }

      @Test
      @WithAnonymousUser
      public void postAuth_badCreds_unauthorized() throws Exception {
          UserAuthRequest request = new UserAuthRequest(BASIC_USER_DETAILS.getUsername(), "bad pass");
          mockMvc.perform(MockMvcRequestBuilders.post(AUTH_ENDPOINT)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectToString(request))
                          .with(SecurityMockMvcRequestPostProcessors.csrf()))
                  .andExpect(status().isUnauthorized());

        assertEquals(0, User.getCachedUsers().size());
      }

      @Test
      @WithBasicUser
      public void deleteAuthenticationToken_success() throws Exception {

          String expectedCookieValue = "sessionId=abc";
          ResponseCookie mockCookie = mock(ResponseCookie.class);
          when(mockCookie.toString()).thenReturn(expectedCookieValue);

          when(jwtSecurityCookieService.getDeleteCookie(any()))
                  .thenReturn(mockCookie);

        mockMvc.perform(MockMvcRequestBuilders.delete(INVALIDATE_AUTH_ENDPOINT)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("sessionId"));
      }

      @Test
      @WithAnonymousUser
      public void deleteAuthenticationToken_unauthenticated_unauthorized() throws Exception {
          mockMvc.perform(MockMvcRequestBuilders.delete(INVALIDATE_AUTH_ENDPOINT)
                          .with(SecurityMockMvcRequestPostProcessors.csrf()))
                  .andExpect(status().isUnauthorized());
      }
}
