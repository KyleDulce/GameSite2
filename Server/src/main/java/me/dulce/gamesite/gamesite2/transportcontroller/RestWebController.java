package me.dulce.gamesite.gamesite2.transportcontroller;

import java.util.UUID;

import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.rooms.managers.Room.RoomListing;
import me.dulce.gamesite.gamesite2.rooms.managers.games.GameType;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.RoomCreateRequest;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.RoomCreateResponse;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.RoomJoinRequest;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.RoomJoinResponse;
import me.dulce.gamesite.gamesite2.transportcontroller.messaging.RoomLeaveRequest;
import me.dulce.gamesite.gamesite2.user.User;

@RestController
public class RestWebController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestWebController.class);

	@Autowired
	private RoomManager roomManager;

	@Autowired
	private AppConfig config;

    @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE")
						.allowedHeaders("*")
						.allowCredentials(true)
						.allowedOrigins(config.getAllowedOriginsAsArray());
			}
		};
	}

    @RequestMapping("/")
    public RedirectView index() {
        return new RedirectView("/" + config.getFrontendPrefixEndpoint());
    }

	//any page in /pages returns static resource
	@RequestMapping("/pages")
	public ModelAndView requestAnyPath() {
		LOGGER.debug("Request for /index.html");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/index.html");
		return mav;
	}

	@RequestMapping("/pages/{requested}")
	public ModelAndView requestAnyPathChild(@PathVariable("requested") String requested) {
		LOGGER.debug("Request for {}", requested);
		if(requested != "" && requested.matches(".+\\..+")) {
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
		LOGGER.debug("GET: getRoomLists");
		return ResponseEntity.ok(roomManager.getAllRoomListings());
	}

	@PostMapping("/api/joinRoom")
	public ResponseEntity<RoomJoinResponse> postJoinRoom(@RequestBody RoomJoinRequest roomJoinRequest) {
		RoomJoinResponse response = new RoomJoinResponse();
		if(roomJoinRequest == null) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		if(roomJoinRequest.user == null || roomJoinRequest.roomId == null || roomJoinRequest.roomId.isEmpty()) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID userUid;
		UUID roomUid;
		try {
			userUid = UUID.fromString(roomJoinRequest.user.uuid);
			roomUid = UUID.fromString(roomJoinRequest.roomId);
		} catch (IllegalArgumentException e) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		User user;
		if(User.cachedUsers.containsKey(userUid)) {
			user = User.cachedUsers.get(userUid);
			//have user leave their room before joining
			UUID roomid = roomManager.getRoomThatContainsUser(user);
			roomManager.processUserLeaveRoomRequest(user, roomid);
		} else {
			user = User.getUserFromMessage(roomJoinRequest.user);
		}

		response.success = roomManager.processUserJoinRoomRequest(user, roomUid, roomJoinRequest.asSpectator);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/leaveRoom")
	public ResponseEntity<Object> postLeaveRoom(@RequestBody RoomLeaveRequest roomLeaveRequest) {
		if(roomLeaveRequest == null || roomLeaveRequest.roomId == null || roomLeaveRequest.user == null) {
			return ResponseEntity.badRequest().build();
		}

		UUID roomId = UUID.fromString(roomLeaveRequest.roomId);
		User user = User.getUserFromMessage(roomLeaveRequest.user);

		if(roomId == null || user == null) {
			return ResponseEntity.badRequest().build();
		}

		roomManager.processUserLeaveRoomRequest(user, roomId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/api/createRoom")
	public ResponseEntity<RoomCreateResponse> postCreateRoom(@RequestBody RoomCreateRequest roomCreateRequest) {
		RoomCreateResponse response = new RoomCreateResponse();
		if(roomCreateRequest == null || roomCreateRequest.user == null || roomCreateRequest.maxLobbySize <= 0) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID userUid;
		try {
			userUid = UUID.fromString(roomCreateRequest.user.uuid);
		} catch (IllegalArgumentException e) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		User user;
		if(User.cachedUsers.containsKey(userUid)) {
			user = User.cachedUsers.get(userUid);
			//have user leave their room before joining
			UUID roomid = roomManager.getRoomThatContainsUser(user);
			roomManager.processUserLeaveRoomRequest(user, roomid);
		} else {
			user = User.getUserFromMessage(roomCreateRequest.user);
		}

		GameType gameType = GameType.getGameTypeFromId(roomCreateRequest.gameType);
		if(user == null || gameType == null) {
			response.success = false;
			return ResponseEntity.badRequest().body(response);
		}

		UUID roomid = roomManager.createRoom(gameType, user, roomCreateRequest.maxLobbySize);
		response.success = roomid != null;
		if(response.success) {
			response.roomId = roomid.toString();
		} else {
			response.roomId = null;
		}
		return ResponseEntity.ok(response);
	}
}