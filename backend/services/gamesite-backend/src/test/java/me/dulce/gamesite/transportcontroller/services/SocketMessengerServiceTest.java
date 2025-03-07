package me.dulce.gamesite.transportcontroller.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import me.dulce.commongames.Room;
import me.dulce.commongames.gamemessage.GameMessengerService;
import me.dulce.commongames.gamemessage.common.ChatMessageMessage;
import me.dulce.game.testgame.TestGameRoom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {SocketMessengerService.class})
class SocketMessengerServiceTest {

    @MockBean private SimpMessagingTemplate simpMessagingTemplate;
    @MockBean private GameMessengerService gameMessengerService;

    @Autowired private SocketMessengerService socketMessengerService;

    @Test
    public void broadcastMessageToRoom_messageSentUsingMessagingTemplate() {

        // assign
        Room room = new TestGameRoom(UUID.randomUUID(), 10, null, "test", gameMessengerService);
        Object data =
                new ChatMessageMessage("This is a test", "Bobby");

        // actual
        socketMessengerService.broadcastMessageToRoom(room, data);

        // assert
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(
                        SocketMessengerService.BROADCAST_DESTINATION + room.getRoomId().toString(),
                        data);
    }
}
