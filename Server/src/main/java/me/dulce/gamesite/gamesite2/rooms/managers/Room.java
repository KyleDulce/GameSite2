package me.dulce.gamesite.gamesite2.rooms.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.dulce.gamesite.gamesite2.user.User;

public abstract class Room {

    private UUID roomid;

    protected User host;
    protected List<User> usersJoinedList;
    protected List<User> spectatorsJoinedList;

    private int maxUsers;

    public abstract String getGameType();

    public Room(UUID roomid, int maxUserCount, User host) {
        this.roomid = roomid;
        this.host = host;
        this.maxUsers = maxUserCount;

        usersJoinedList = new ArrayList<>();
        spectatorsJoinedList = new ArrayList<>();
    }
    
}
