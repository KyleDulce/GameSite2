package me.dulce.gamesite.gamesite2.transportcontroller.services;

import me.dulce.gamesite.gamesite2.rooms.managers.Room;
import me.dulce.gamesite.gamesite2.rooms.managers.games.TestGame;
import me.dulce.gamesite.gamesite2.rooms.managers.games.common.chatmessage.ChatMessageData;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocketMessengerServiceTest {

    @Test
    public void broadcastMessageToRoom_messageSentUsingMessagingTemplate(){

        //assign
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        SocketMessengerService messengerService = new SocketMessengerService(simpMessagingTemplate);
        Room room = new TestGame(UUID.randomUUID(), 10, null, "test", messengerService);
        Object data = new ChatMessageData(room.getRoomid(), "This is a test", "Bobbert")
                .parseObjectToDataMessage();

        //actual
        messengerService.broadcastMessageToRoom(room, data);

        //assert
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                SocketMessengerService.BROADCAST_DESTINATION + room.getRoomid().toString(),
                data);

    }

}