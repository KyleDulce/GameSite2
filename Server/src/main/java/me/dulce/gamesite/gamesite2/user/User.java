package me.dulce.gamesite.gamesite2.user;

import java.time.Instant;
import java.util.*;
import lombok.*;
import me.dulce.gamesite.gamesite2.security.UserSecurityDetails;
import org.jetbrains.annotations.TestOnly;

/** Class Representing Generic User */
@Getter
public class User {

    /** Users that are temporarily cached */
    @Getter private static final HashMap<UUID, User> cachedUsers = new HashMap<>();

    private static final Random random = new Random();

    /**
     * Creates new user from uuid and name
     *
     * @param uuid uuid of user
     * @param name name of user
     * @return user object
     */
    public static User createNewUser(UUID uuid, String name, String sessionId) {
        User user;
        if (!cachedUsers.containsKey(uuid)) {
            user = new User(uuid, name, sessionId);
            addUserToCache(user);
        } else {
            user = cachedUsers.get(uuid);
            user.setSessionId(sessionId);
            user.setName(name);
            user.setSocketId(null);
        }
        return user;
    }

    /**
     * Gets a user based on their uuid. returns null if their cached session is not available
     *
     * @param uuid the uuid to search
     * @return Option with the user, empty if user is not available
     */
    public static Optional<User> getUserFromUUID(UUID uuid) {
        if (cachedUsers.containsKey(uuid)) {
            return Optional.of(cachedUsers.get(uuid));
        }
        return Optional.empty();
    }

    /**
     * Gets a user based on their uuid. returns null if their cached session is not available
     *
     * @param uuid the uuid to search
     * @return Option with the user, empty if user is not available
     */
    public static Optional<User> getUserFromUUID(String uuid) {
        UUID uidObj = UUID.fromString(uuid);
        return getUserFromUUID(uidObj);
    }

    public static void addUserToCache(User user) {
        cachedUsers.put(user.getUuid(), user);
    }

    public static User getUserFromSecurityDetails(UserSecurityDetails userSecurityDetails) {
        Optional<User> user = getUserFromUUID(userSecurityDetails.getUserId());
        return user.orElseGet(
                () ->
                        createNewUser(
                                userSecurityDetails.getUserId(),
                                "", // TODO
                                null));
    }

    private User(UUID uid, String name, String sessionId) {
        this.uuid = uid;
        this.name = name;
        this.sessionId = sessionId;
    }

    private final UUID uuid;
    @Setter private String name;
    @Setter private String sessionId;
    @Setter private String socketId = null;
    private Instant lastActiveTime = null;

    /** updates lastActiveTime for user to current Instant */
    public void updateActiveTime(Instant time) {
        lastActiveTime = time;
    }

    /**
     * Converts User into a serializable version
     *
     * @return
     */
    public UserMessage toMessageableObject() {
        UserMessage message = new UserMessage();
        message.uuid = uuid.toString();
        message.name = name;

        return message;
    }

    public boolean equals(User user) {
        if (user == null) {
            return false;
        }
        return uuid.equals(user.getUuid());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        return equals((User) other);
    }

    /** Serializable version of a user */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMessage {
        public String uuid;
        public String name;
        public boolean isGuest;
    }

    @TestOnly
    public static void clearCache() {
        cachedUsers.clear();
    }
}
