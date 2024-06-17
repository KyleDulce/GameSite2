package me.dulce.commongames.messaging;

public enum SocketDestinations {
    GAMEDATA("/gamePlayer");

    private final String endpoint;

    SocketDestinations(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return "/queue" + endpoint;
    }
}
