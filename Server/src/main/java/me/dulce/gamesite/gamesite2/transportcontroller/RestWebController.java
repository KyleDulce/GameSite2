package me.dulce.gamesite.gamesite2.transportcontroller;

import static me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils.getUUIDFromString;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.rooms.Room;
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
@RequestMapping("/api")
public class RestWebController {

    @Autowired private RoomManager roomManager;

    @Autowired private AppConfig config;

    @Autowired private AuthenticationManager authenticationManager;

    @Autowired private JwtSecurityCookieService jwtSecurityCookieService;

    private final Random random = new Random();

    @GetMapping("/getRoomLists")
    @Operation(summary = "Get a list of available rooms")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms", content = @Content(
                    mediaType = "application.json", array = @ArraySchema(schema = @Schema(implementation = RoomListing.class))
            )),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content)
    })
    public ResponseEntity<RoomListing[]> getRoomLists() {

        User user = getUserFromAuthentication();

        return ResponseEntity.ok(roomManager.getAllRoomListings());
    }

    @PostMapping("/joinRoom/{roomId}")
    @Operation(summary = "Joins a room. Returns whether or not action was successful")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully joined room", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to join the room", content = @Content),
    })
    public ResponseEntity<RoomJoinResponse> postJoinRoom(
            @Parameter(description = "The id of the room", required = true) @PathVariable String roomId,
            @Parameter(description = "Whether player should join as a spectator") @RequestParam(defaultValue = "false") boolean asSpectator) {

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
            response.success =
                    roomManager.processUserJoinRoomRequest(user, roomUid.get(), asSpectator);
        } else {
            // already in the room
            Room curRoom = roomManager.getRoomFromUUID(roomUid.get());
            // if they are joining as a spec and is a reg player, leave
            // same vice versa
            // otherwise they are already in the room
            if ((asSpectator && curRoom.getUsersJoinedList().contains(user))
                    || ((!asSpectator) && curRoom.getSpectatorsJoinedList().contains(user))) {
                roomManager.processUserLeaveRoomRequest(user, roomUid.get());
                response.success =
                        roomManager.processUserJoinRoomRequest(user, roomUid.get(), asSpectator);
            } else {
                response.success = true;
            }
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/leaveRoom/{roomId}")
    @Operation(summary = "Leaves a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully joined room", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Tried to leave a room that the given player is not part of", content = @Content),
    })
    public ResponseEntity<?> postLeaveRoom(
            @Parameter(description = "The id of the room to lave")
            @PathVariable String roomId) {

        Optional<UUID> roomIdOptional = getUUIDFromString(roomId);
        if (roomIdOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = getUserFromAuthentication();
        roomManager.processUserLeaveRoomRequest(user, roomIdOptional.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/createRoom")
    @Operation(summary = "Creates a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully creates a room", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomCreateResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request. Read body", content = @Content(mediaType = "text/plain")),
    })
    public ResponseEntity<?> postCreateRoom(
            @RequestParam int maxLobbySize,
            @RequestParam String gameType,
            @RequestParam(required = false) String roomName) {

        RoomCreateResponse response = new RoomCreateResponse();
        if (maxLobbySize <= 0) {
            return ResponseEntity.badRequest().body("Cannot create lobby of size <= 0");
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
            return ResponseEntity.badRequest().body("Invalid game type");
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

    @GetMapping("/roomInfo/{roomId}")
    @Operation(summary = "Gets information about a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful on getting room information", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request. Read body", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Room Id does not exist", content = @Content),
    })
    public ResponseEntity<?> getRoomInfo(@PathVariable String roomId) {
        Optional<UUID> uuid = getUUIDFromString(roomId);
        if (uuid.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid roomId format. Must be a UUID");
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

    @GetMapping("/refreshToken")
    @Operation(summary = "Refreshes the authentication cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful", content = @Content, headers = @Header(name = "Set-Cookie")),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content)
    })
    public ResponseEntity<?> getRefreshToken() {
        return createSuccessResponseEntity(
                jwtSecurityCookieService
                        .generateNewResponseCookie(
                                SecurityContextHolder.getContext().getAuthentication())
                        .toString());
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticates the user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful", content = @Content, headers = @Header(name = "Set-Cookie")),
            @ApiResponse(responseCode = "400", description = "Authentication information was not provided", content = @Content),
            @ApiResponse(responseCode = "401", description = "Bad login or password", content = @Content)
    })
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

    @DeleteMapping("/invalidateAuthentication")
    @Operation(summary = "Invalidates token and session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful", content = @Content, headers = @Header(name = "Set-Cookie")),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content)
    })
    public ResponseEntity<?> deleteAuthenticationToken() {
        User user = getUserFromAuthentication();
        return createSuccessResponseEntity(
                jwtSecurityCookieService.getDeleteCookie(user).toString());
    }

    private User getUserFromAuthentication() {
        return GamesiteUtils.getUserSecurityDetailsFromPrincipal(
                SecurityContextHolder.getContext().getAuthentication());
    }

    private <T> ResponseEntity<T> createSuccessResponseEntity(String cookieValue) {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieValue).build();
    }
}
