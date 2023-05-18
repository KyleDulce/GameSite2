package me.dulce.gamesite.gamesite2.user;

import lombok.Getter;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Class Representing Generic User
 */
@Getter
public class User {

    /**
     * Users that are temporarily cached
     */
    public static HashMap<UUID, User> cachedUsers = new HashMap<>();

    /**
     * Deprecated for removal, guest users are no longer going to be supported
     * Creates a user of type guest
     * @return
     */
    @Deprecated(forRemoval = true)
    public static User createGuestUser() {
        UUID generatedUuid = UUID.randomUUID();
        String generatedName = "Guest#" + new Random().nextInt(10000);
        return new User(generatedUuid, generatedName, true);
    }

    /**
     * Gets a user object from the user message
     * @param message
     * @return
     */
    public static User getUserFromMessage(UserMessage message) {
        UUID uidObject = UUID.fromString(message.uuid);
        if(cachedUsers.containsKey(uidObject)) {
            return cachedUsers.get(uidObject);
        } else {
            return new User(UUID.fromString(message.uuid), message.name, message.isGuest);
        }
    }

    private User(UUID uid, String name, boolean guest) {
        this.uuid = uid;
        this.name = name;
        this.isGuest = guest;
    }

    private UUID uuid;
    private String name;
    /**
     * Deprecated for removal, guest users are no longer supported
     */
    @Deprecated(forRemoval = true)
    private boolean isGuest;
    private String sessionId = null;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof User && uuid.equals(((User) obj).uuid));
    }

    /**
     * Converts User into a serializable version
     * @return
     */
    public UserMessage toMessagableObject() {
        UserMessage message = new UserMessage();
        message.uuid = uuid.toString();
        message.name = name;
        message.isGuest = isGuest;
        
        return message;
    }

    /**
     * Serializable version of a user
     */
    public static class UserMessage {
        public String uuid;
        public String name;
        public boolean isGuest;
    }
}
