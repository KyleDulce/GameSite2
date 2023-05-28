package me.dulce.gamesite.gamesite2.transportcontroller;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.managers.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.user.User;

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

    @RequestMapping("/")
    public RedirectView index() {
        return new RedirectView("/" + config.getFrontendPrefixEndpoint());
    }

	@RequestMapping("{requested}")
	public ModelAndView requestAnyPathChild(@PathVariable("requested") String requested) {
		LOGGER.debug("Request for {}", requested);
		if(!GamesiteUtils.isBlank(requested) && requested.matches(".+\\..+")) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName(requested);
			return mav;
		} else {
			ModelAndView mav = new ModelAndView();
			mav.setViewName(String.format("/%s", config.getFrontendPrefixEndpoint()));
			return mav;	
		}
	}

	@GetMapping("/api/getRoomLists")
	public ResponseEntity<RoomListing[]> getRoomLists() {
		return ResponseEntity.ok(roomManager.getAllRoomListings());
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

		Optional<UUID> roomUid = GamesiteUtils.getUUIDFromString(roomJoinRequest.roomId);
		if(roomUid.isEmpty()) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID duplicateRoomId = roomManager.getRoomThatContainsUser(userOptional.get());
		if(duplicateRoomId != null) {
			roomManager.processUserLeaveRoomRequest(userOptional.get(), duplicateRoomId);
		}

		response.success = roomManager.processUserJoinRoomRequest(userOptional.get(),
				roomUid.get(),
				roomJoinRequest.asSpectator);

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

		Optional<UUID> roomIdOptional = GamesiteUtils.getUUIDFromString(roomLeaveRequest.roomId);

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
		ResponseCookie cookie = cookieService.getUserCookie(userOptional.get());
		return createSuccessResponseEntity(cookie.toString());
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
			user = User.createNewUser(result.get());
		} else {
			user = User.createNewUser(result.get(), optionalName.get());
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
