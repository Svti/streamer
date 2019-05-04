package com.streamer.service.core;

public enum StreamerRole {

    UNKNOWN(0), MASTER(1), SLAVE(2);

    private int role;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    StreamerRole(int role) {
        this.role = role;
    }

    public static StreamerRole valOf(int status) {
        StreamerRole current = StreamerRole.UNKNOWN;
        for (StreamerRole role : values()) {
            if (role.getRole() == role.getRole()) {
                current = role;
            }
        }
        return current;
    }
}
