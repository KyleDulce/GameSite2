package me.dulce.game.testgame;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.game.GameResolver;
import me.dulce.commongames.game.GameType;
import me.dulce.commongames.gamemessage.GameMessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import java.util.UUID;

/** Gametype for testing purposes */
@GameType(gameId = "Test-TestGame", displayName = "Testing Game")
public class TestGameRoom extends Room {

    public static final String GAME_ID = "Test-TestGame";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestGameRoom.class);

    public TestGameRoom(
            UUID roomId,
            int maxUserCount,
            User host,
            String roomName,
            GameMessengerService messengerService) {
        super(roomId, maxUserCount, host, roomName, messengerService);
    }

    @Override
    public String getGameId() {
        return GAME_ID;
    }

    @Override
    public void onUserJoinEvent(User user) {}

    @Override
    public void onSpectatorJoinEvent(User user) {}

    @Override
    public void onUserLeaveEvent(User user) {}

    @EventListener
    public void onTestMessage(User user, TestMessage testMessage) {
        LOGGER.info("TEST MESSAGE");
        gameMessengerService.sendToUser(user, this, testMessage);
    }
}
