package me.dulce.commongames;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static final String user1UUID_str = "eb0f39e0-d108-4bc9-83cd-1e12d4b0c784";
    private static UUID user1UUID;

    @BeforeAll
    public static void beforeTests() {
        user1UUID = UUID.fromString(user1UUID_str);
    }

    @Test
    public void toMessageableObject_userObjectConvertsToUserMessageObject_expectSameData() {
        // assign
        String expectedName = "SomeName";
        String expectedSession = "SomeSession";
        User userObj = User.createNewUser(user1UUID, expectedName, expectedSession);

        // actual
        User.UserMessage actual = userObj.toMessageableObject();

        // assert
        assertEquals(user1UUID_str, actual.uuid);
        assertEquals(expectedName, actual.name);
    }
}
