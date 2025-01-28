package org.example.TCP;

public class CurrentSession {
    private static CurrentSession instance;
    private int currentProfileId;

    private CurrentSession() {}

    public static CurrentSession getInstance() {
        if (instance == null) {
            instance = new CurrentSession();
        }
        return instance;
    }

    public int getCurrentProfileId() {
        return currentProfileId;
    }

    public void setCurrentProfileId(int id) {
        this.currentProfileId = id;
    }
}

