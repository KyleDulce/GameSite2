package me.dulce.gamesite.gamesite2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/** Entry point of application */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class Gamesite2Application {

  public static void main(String[] args) {
    SpringApplication.run(Gamesite2Application.class, args);
  }
}
