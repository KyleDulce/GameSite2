package me.dulce.gamesite.user;

import me.dulce.commongames.User;
import me.dulce.commonutils.DateUtils;
import me.dulce.gamesite.configuration.AppConfig;
import me.dulce.gamesite.rooms.RoomManager;
import me.dulce.gamesite.utilservice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
                    || !DateUtils.isInstantWithinAge(
                            user.getLastActiveTime(),
                            config.getUserActivityTimeoutSeconds(),
                            now)) {
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
