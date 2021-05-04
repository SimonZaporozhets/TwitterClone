package ru.startandroid.develop.twitterclone;

public class User {

    private String username;
    private boolean noFollowing;

    public User() {
    }

    public User(String username, boolean noFollowing) {
        this.username = username;
        this.noFollowing = noFollowing;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isNoFollowing() {
        return noFollowing;
    }

    public void setNoFollowing(boolean noFollowing) {
        this.noFollowing = noFollowing;
    }

    @Override
    public String toString() {
        return username;
    }
}
