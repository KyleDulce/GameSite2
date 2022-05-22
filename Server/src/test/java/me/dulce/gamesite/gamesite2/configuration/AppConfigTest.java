package me.dulce.gamesite.gamesite2.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppConfigTest {

    @Autowired
    private AppConfig config;

    @Test
    public void getAllowedOrigins_readsYamlCorrectly() {
        List<String> actual = config.getAllowedOrigins();

        System.out.println(actual.get(0));

        assertEquals(3, actual.size());
        assertEquals("http://localhost:8080", actual.get(0));
        assertEquals("http://localhost:4200", actual.get(1));
        assertEquals("null", actual.get(2));
    }
}
