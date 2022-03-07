package me.dulce.gamesite.gamesite2.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import me.dulce.gamesite.gamesite2.user.User.UserMessage;

@SpringBootTest
public class UserTest {

    @Test
    public void toMessagableObject_userObjectConvertsToUserMessageObject_expectSameData() {
        //assign
        User userObj = User.createGuestUser();
        String expectedUuid = userObj.getuuid().toString();
        String expectedName = userObj.getName();
        boolean expectedGuestStatus = userObj.getGuestState();

        //actual
        UserMessage actual = userObj.toMessagableObject();
        
        //assert
        assertEquals(expectedUuid, actual.uuid);
        assertEquals(expectedName, actual.name);
        assertEquals(expectedGuestStatus, actual.isGuest);
    }

    @Test
    public void getUserFromMessage_userMessageObjectConvertToUserObject_expectSameData() {
        //assign
        User originalUserObj = User.createGuestUser();
        UUID expectedUuid = originalUserObj.getuuid();
        String expectedName = originalUserObj.getName();
        boolean expectedGuestStatus = originalUserObj.getGuestState();
        UserMessage userMessageObj = originalUserObj.toMessagableObject();

        //actual
        User actual = User.getUserFromMessage(userMessageObj);

        //assert
        assertEquals(expectedUuid, actual.getuuid());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedGuestStatus, actual.getGuestState());
    }
}
