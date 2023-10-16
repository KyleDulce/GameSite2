package me.dulce.gamesite.gamesite2.transportcontroller;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import me.dulce.gamesite.gamesite2.rooms.Room;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.*;
import me.dulce.gamesite.gamesite2.transportcontroller.services.AuthService;
import me.dulce.gamesite.gamesite2.transportcontroller.services.CookieService;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

import static me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils.getUUIDFromString;

/**
 * Controller that handles rest requests
 */
@RestController
public class RestWebController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestWebController.class);

	@Autowired
	private RoomManager roomManager;

	@Autowired
	private AppConfig config;

	@Autowired
	private AuthService authService;

	@Autowired
	private CookieService cookieService;

	private final Random random = new Random();

	@GetMapping("/api/getRoomLists")
	public ResponseEntity<RoomListing[]> getRoomLists(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(roomManager.getAllRoomListings(), cookie.toString());
	}

	@PostMapping("/api/joinRoom")
	public ResponseEntity<RoomJoinResponse> postJoinRoom(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@RequestBody RoomJoinRequest roomJoinRequest) {

		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		RoomJoinResponse response = new RoomJoinResponse();
		if(userOptional.isEmpty()) {
			response.success = false;
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		if(roomJoinRequest == null) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		if(roomJoinRequest.roomId == null || roomJoinRequest.roomId.isEmpty()) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		Optional<UUID> roomUid = getUUIDFromString(roomJoinRequest.roomId);
		if(roomUid.isEmpty()) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID duplicateRoomId = roomManager.getRoomThatContainsUser(userOptional.get());
		if(duplicateRoomId != null && !roomUid.get().equals(duplicateRoomId)) {
			roomManager.processUserLeaveRoomRequest(userOptional.get(), duplicateRoomId);
		}

		if(!roomUid.get().equals(duplicateRoomId)) {
			// not already in the room
			response.success = roomManager.processUserJoinRoomRequest(userOptional.get(),
					roomUid.get(),
					roomJoinRequest.asSpectator);
		} else {
			// already in the room
			Room curRoom = roomManager.getRoomFromUUID(roomUid.get());
			// if they are joining as a spec and is a reg player, leave
			// same vice versa
			// otherwise they are already in the room
			if((roomJoinRequest.asSpectator && curRoom.getUsersJoinedList().contains(userOptional.get())) ||
					((!roomJoinRequest.asSpectator) && curRoom.getSpectatorsJoinedList().contains(userOptional.get()))) {
				roomManager.processUserLeaveRoomRequest(userOptional.get(), roomUid.get());
				response.success = roomManager.processUserJoinRoomRequest(userOptional.get(),
						roomUid.get(),
						roomJoinRequest.asSpectator);
			} else {
				response.success = true;
			}
		}

		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@PostMapping("/api/leaveRoom")
	public ResponseEntity<Object> postLeaveRoom(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@RequestBody RoomLeaveRequest roomLeaveRequest) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if(roomLeaveRequest == null || roomLeaveRequest.roomId == null) {
			return ResponseEntity.badRequest().build();
		}

		Optional<UUID> roomIdOptional = getUUIDFromString(roomLeaveRequest.roomId);

		if(roomIdOptional.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		roomManager.processUserLeaveRoomRequest(userOptional.get(), roomIdOptional.get());
		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(cookie.toString());
	}

	@PostMapping("/api/createRoom")
	public ResponseEntity<RoomCreateResponse> postCreateRoom(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@RequestBody RoomCreateRequest roomCreateRequest) {

		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		RoomCreateResponse response = new RoomCreateResponse();
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		if(roomCreateRequest == null || roomCreateRequest.maxLobbySize <= 0) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		String roomName = roomCreateRequest.roomName;
		if(GamesiteUtils.isBlank(roomName)) {
			roomName = "room" + random.nextInt(1000000);
		}

		UUID duplicateRoomId = roomManager.getRoomThatContainsUser(userOptional.get());
		if(duplicateRoomId != null) {
			roomManager.processUserLeaveRoomRequest(userOptional.get(), duplicateRoomId);
		}

		GameType gameType = GameType.getGameTypeFromId(roomCreateRequest.gameType);
		if(gameType == null) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID roomId = roomManager.createRoom(gameType, userOptional.get(), roomCreateRequest.maxLobbySize, roomName);
		response.success = roomId != null;
		if(response.success) {
			response.roomId = roomId.toString();
		} else {
			response.roomId = null;
		}

		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@GetMapping("/api/roomInfo/{roomId}")
	public ResponseEntity<RoomInfoResponse> getRoomInfo(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@PathVariable String roomId
	) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<UUID> uuid = getUUIDFromString(roomId);
		if(uuid.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		Room room = roomManager.getRoomFromUUID(uuid.get());
		if(room == null) {
			return ResponseEntity.notFound().build();
		}

		RoomInfoResponse response = new RoomInfoResponse();
		response.room = room.getRoomListingObject();
		response.isHost = room.getHost().equals(userOptional.get());
		response.joinedRoom = room.getUsersJoinedList().contains(userOptional.get());
		response.isSpectating = room.getSpectatorsJoinedList().contains(userOptional.get());

		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@PostMapping("/api/updateUser")
	public ResponseEntity<UserUpdateResponse> postUpdateUser(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@RequestBody UserUpdateRequest userUpdateRequest) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		UserUpdateResponse response = new UserUpdateResponse();
		if(userOptional.isEmpty()) {
			response.success = false;
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		if(GamesiteUtils.isBlank(userUpdateRequest.name)) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		userOptional.get().setName(userUpdateRequest.name);
		response.success = true;
		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@GetMapping("/api/refreshToken")
	public ResponseEntity<?> getRefreshToken(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		UserAuthResponse response = new UserAuthResponse();
		response.success = true;
		response.user = userOptional.get().toMessageableObject();

		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@PostMapping("/api/authenticate")
	public ResponseEntity<UserAuthResponse> postAuth(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt,
			@RequestBody UserAuthRequest userAuthRequest) {
		UserAuthResponse response = new UserAuthResponse();
		if(userAuthRequest == null) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		Optional<UUID> result = authService.validateAuthCreds(userAuthRequest.login, userAuthRequest.passHash);
		if(result.isEmpty()) {
			response.success = false;
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		Optional<String> optionalName = cookieService.getNameFromCookie(cookieJwt);
		User user;
		if(optionalName.isEmpty()) {
			user = User.createNewUser(result.get(), config.getCookieIdBuffer());
		} else {
			user = User.createNewUser(result.get(), optionalName.get(), config.getCookieIdBuffer());
		}
		User.addUserToCache(user);

		response.success = true;
		response.user = user.toMessageableObject();

		ResponseCookie cookie = cookieService.getUserCookie(user);
		return createSuccessResponseEntity(response, cookie.toString());
	}

	@DeleteMapping("/api/invalidateAuthentication")
	public ResponseEntity<?> deleteAuthenticationToken(
			@CookieValue(name = CookieService.AUTH_COOKIE_ID, defaultValue = "bad-cookie") String cookieJwt) {
		Optional<User> userOptional = cookieService.validateUserCookie(cookieJwt);
		if(userOptional.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		ResponseCookie cookie = cookieService.getDeleteCookie(userOptional.get());
		return createSuccessResponseEntity(cookie.toString());
 	}

	private <T> ResponseEntity<T> createSuccessResponseEntity(T body, String cookieValue) {
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookieValue)
				.body(body);
	}

	private <T> ResponseEntity<T> createSuccessResponseEntity(String cookieValue) {
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookieValue)
				.build();
	}
}
