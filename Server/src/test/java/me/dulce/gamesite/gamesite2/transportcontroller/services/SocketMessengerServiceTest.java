package me.dulce.gamesite.gamesite2.transportcontroller.services;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.testgame.TestGame;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage.ChatMessageData;
import me.dulce.gamesite.gamesite2.transportcontroller.SocketController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {SocketMessengerService.class})
class SocketMessengerServiceTest {

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SocketMessengerService socketMessengerService;

    @Test
    public void broadcastMessageToRoom_messageSentUsingMessagingTemplate(){

        //assign
        Room room = new TestGame(UUID.randomUUID(), 10, null, "test", socketMessengerService);
        Object data = new ChatMessageData(room.getRoomId(), "This is a test", "Bobby")
                .parseObjectToDataMessage();

        //actual
        socketMessengerService.broadcastMessageToRoom(room, data);

        //assert
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                SocketMessengerService.BROADCAST_DESTINATION + room.getRoomId().toString(),
                data);

    }

}