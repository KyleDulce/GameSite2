package me.dulce.gamesite;

import me.dulce.commongames.CommonGamesReference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/** Entry point of application */
@SpringBootApplication
@Import(CommonGamesReference.class)
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ComponentScan({"me.dulce.gamesite"})
public class Gamesite2Application {

    public static void main(String[] args) {
        SpringApplication.run(Gamesite2Application.class, args);
    }
}
