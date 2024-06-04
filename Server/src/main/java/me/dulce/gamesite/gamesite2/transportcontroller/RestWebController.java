package me.dulce.gamesite.gamesite2.transportcontroller;

import static me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils.getUUIDFromString;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.rooms.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.security.JwtSecurityCookieService;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.*;
import me.dulce.gamesite.gamesite2.user.User;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/** Controller that handles rest requests */
@RestController
public class RestWebController {

  @Autowired private RoomManager roomManager;

  @Autowired private AppConfig config;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private JwtSecurityCookieService jwtSecurityCookieService;

  private final Random random = new Random();

  @GetMapping("/api/getRoomLists")
  public ResponseEntity<RoomListing[]> getRoomLists() {

    User user = getUserFromAuthentication();

    return ResponseEntity.ok(roomManager.getAllRoomListings());
  }

  @PostMapping("/api/joinRoom/{roomId}")
  public ResponseEntity<RoomJoinResponse> postJoinRoom(
      @PathVariable String roomId, @RequestParam(defaultValue = "false") boolean asSpectator) {

    Optional<UUID> roomUid = getUUIDFromString(roomId);
    if (roomUid.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    User user = getUserFromAuthentication();

    UUID duplicateRoomId = roomManager.getRoomThatContainsUser(user);
    if (duplicateRoomId != null && !roomUid.get().equals(duplicateRoomId)) {
      roomManager.processUserLeaveRoomRequest(user, duplicateRoomId);
    }

    RoomJoinResponse response = new RoomJoinResponse();
    if (!roomUid.get().equals(duplicateRoomId)) {
      // not already in the room
      response.success = roomManager.processUserJoinRoomRequest(user, roomUid.get(), asSpectator);
    } else {
      // already in the room
      Room curRoom = roomManager.getRoomFromUUID(roomUid.get());
      // if they are joining as a spec and is a reg player, leave
      // same vice versa
      // otherwise they are already in the room
      if ((asSpectator && curRoom.getUsersJoinedList().contains(user))
          || ((!asSpectator) && curRoom.getSpectatorsJoinedList().contains(user))) {
        roomManager.processUserLeaveRoomRequest(user, roomUid.get());
        response.success = roomManager.processUserJoinRoomRequest(user, roomUid.get(), asSpectator);
      } else {
        response.success = true;
      }
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/api/leaveRoom/{roomId}")
  public ResponseEntity<?> postLeaveRoom(@PathVariable String roomId) {

    Optional<UUID> roomIdOptional = getUUIDFromString(roomId);
    if (roomIdOptional.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    User user = getUserFromAuthentication();
    roomManager.processUserLeaveRoomRequest(user, roomIdOptional.get());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/createRoom")
  public ResponseEntity<RoomCreateResponse> postCreateRoom(
      @RequestParam int maxLobbySize,
      @RequestParam String gameType,
      @RequestParam String roomName) {

    RoomCreateResponse response = new RoomCreateResponse();
    if (maxLobbySize <= 0) {
      return ResponseEntity.badRequest().build();
    }

    if (GamesiteUtils.isBlank(roomName)) {
      roomName = "room" + random.nextInt(1000000);
    }

    User user = getUserFromAuthentication();

    UUID duplicateRoomId = roomManager.getRoomThatContainsUser(user);
    if (duplicateRoomId != null) {
      roomManager.processUserLeaveRoomRequest(user, duplicateRoomId);
    }

    GameType gameTypeObj = GameType.getGameTypeFromId(gameType);
    if (gameTypeObj == null) {
      return ResponseEntity.badRequest().build();
    }

    UUID roomId = roomManager.createRoom(gameTypeObj, user, maxLobbySize, roomName);
    response.success = roomId != null;
    if (response.success) {
      response.roomId = roomId.toString();
    } else {
      response.roomId = null;
    }

    return ResponseEntity.ok(response);
  }

  @GetMapping("/api/roomInfo/{roomId}")
  public ResponseEntity<RoomInfoResponse> getRoomInfo(@PathVariable String roomId) {
    Optional<UUID> uuid = getUUIDFromString(roomId);
    if (uuid.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    Room room = roomManager.getRoomFromUUID(uuid.get());
    if (room == null) {
      return ResponseEntity.notFound().build();
    }

    User user = getUserFromAuthentication();
    RoomInfoResponse response = new RoomInfoResponse();
    response.room = room.getRoomListingObject();
    response.isHost = room.getHost().equals(user);
    response.joinedRoom = room.getUsersJoinedList().contains(user);
    response.isSpectating = room.getSpectatorsJoinedList().contains(user);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/api/refreshToken")
  public ResponseEntity<?> getRefreshToken() {
    return createSuccessResponseEntity(
        jwtSecurityCookieService
            .generateNewResponseCookie(SecurityContextHolder.getContext().getAuthentication())
            .toString());
  }

  @PostMapping("/api/authenticate")
  public ResponseEntity<?> postAuth(@RequestBody UserAuthRequest userAuthRequest) {
    if (userAuthRequest == null) {
      return ResponseEntity.badRequest().build();
    }

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userAuthRequest.login, userAuthRequest.password));

    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    User.addUserToCache(getUserFromAuthentication());

    return createSuccessResponseEntity(
        jwtSecurityCookieService.generateNewResponseCookie(authentication).toString());
  }

  @DeleteMapping("/api/invalidateAuthentication")
  public ResponseEntity<?> deleteAuthenticationToken() {
    User user = getUserFromAuthentication();
    return createSuccessResponseEntity(jwtSecurityCookieService.getDeleteCookie(user).toString());
  }

  private User getUserFromAuthentication() {
    return GamesiteUtils.getUserSecurityDetailsFromPrincipal(
        SecurityContextHolder.getContext().getAuthentication());
  }

  private <T> ResponseEntity<T> createSuccessResponseEntity(String cookieValue) {
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieValue).build();
  }
}
