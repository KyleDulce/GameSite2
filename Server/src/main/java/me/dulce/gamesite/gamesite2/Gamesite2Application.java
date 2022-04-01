package me.dulce.gamesite.gamesite2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.dulce.gamesite.gamesite2.rooms.RoomManager;

@SpringBootApplication
public class Gamesite2Application {

	@Autowired
	public RoomManager roomManager;

	public static void main(String[] args) {
		SpringApplication.run(Gamesite2Application.class, args);
	}

	public Gamesite2Application() {}

}
