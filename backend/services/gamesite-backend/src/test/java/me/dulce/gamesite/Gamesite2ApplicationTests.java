package me.dulce.gamesite;

import me.dulce.gamesite.testutils.SpringSecurityTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
@Import(Gamesite2Application.class)
class Gamesite2ApplicationTests {

    @Test
    void contextLoads() {}
}
