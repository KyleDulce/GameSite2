package me.kyledulce.gamesite.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "application.yml")
@EnableTask
public class UserRegistrationToolApplication {

    @Bean

    public static void main(String[] args) {
        SpringApplication.run(UserRegistrationToolApplication.class, args);
    }
}
