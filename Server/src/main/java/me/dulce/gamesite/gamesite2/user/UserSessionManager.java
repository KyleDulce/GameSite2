package me.dulce.gamesite.gamesite2.user;

import java.time.Instant;
import java.util.*;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.rooms.RoomManager;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import me.dulce.gamesite.gamesite2.utilservice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserSessionManager {

  private final TimeService timeService;
  private final RoomManager roomManager;
  private final AppConfig config;
  private Random randomObj;

  private Set<String> validSessions;

  @Autowired
  public UserSessionManager(TimeService timeService, RoomManager roomManager, AppConfig config) {
    this.timeService = timeService;
    this.roomManager = roomManager;
    this.config = config;

    randomObj = new Random();
    validSessions = new HashSet<>();
  }

  @Scheduled(fixedDelayString = "${auth.cacheClearIntervalSeconds}")
  public void clearCachedUsersTask() {
    Collection<User> users = User.getCachedUsers().values();
    Instant now = timeService.getCurrentInstant();
    for (User user : users) {
      if (user.getLastActiveTime() == null
          || !GamesiteUtils.isInstantWithinAge(
              user.getLastActiveTime(), config.getUserActivityTimeoutSeconds(), now)) {
        User.getCachedUsers().remove(user.getUuid());
        // kick user from rooms
        UUID roomId = roomManager.getRoomThatContainsUser(user);
        roomManager.processUserLeaveRoomRequest(user, roomId);
        validSessions.remove(user.getSessionId());
      }
    }
    randomObj = new Random();
  }

  public boolean isValidSession(String sessionId) {
    return validSessions.contains(sessionId);
  }

  public String generateNewSession(User user) {
    String sessionId = user.getUuid().toString() + randomObj.nextLong(1000000000);
    if (isValidSession(user.getSessionId())) {
      validSessions.remove(user.getSessionId());
    }
    validSessions.add(sessionId);
    user.setSessionId(sessionId);

    return sessionId;
  }
}