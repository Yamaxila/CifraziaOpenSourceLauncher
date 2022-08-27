package io.github.yamaxila.cifrazia.models;

import io.github.yamaxila.cifrazia.cats.models.AuthResponse;

public class UserModel {

    private AuthResponse userCredentials;
    private String username, uuid;
    private long lastUpdate;

    public UserModel(AuthResponse userCredentials, String username, String uuid) {
        this.userCredentials = userCredentials;
        this.username = username;
        this.uuid = uuid;
        this.lastUpdate = System.currentTimeMillis();
    }

    public AuthResponse getUserCredentials() {
        return userCredentials;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public long getLastUpdateTime() {
        return lastUpdate;
    }

    public void update(AuthResponse authenticateData) {
        this.userCredentials = authenticateData;
        this.lastUpdate = System.currentTimeMillis();
    }
}
