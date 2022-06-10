package me.dulce.gamesite.gamesite2.user;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class User {

    public static HashMap<UUID, User> cachedUsers = new HashMap<>();

    public static User createGuestUser() {
        UUID generatedUuid = UUID.randomUUID();
        String generatedName = "Guest#" + new Random().nextInt(10000);
        return new User(generatedUuid, generatedName, true);
    }

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
    private boolean isGuest;
    private String sessionId = null;

    public UUID getuuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean getGuestState() {
        return isGuest;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof User && uuid.equals(((User) obj).uuid));
    }

    public UserMessage toMessagableObject() {
        UserMessage message = new UserMessage();
        message.uuid = uuid.toString();
        message.name = name;
        message.isGuest = isGuest;
        
        return message;
    }

    public static class UserMessage {
        public String uuid;
        public String name;
        public boolean isGuest;
    }
}
