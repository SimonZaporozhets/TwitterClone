package ru.startandroid.develop.twitterclone;

public class Tweet {

    private String content;
    private String time;

    public Tweet() {
    }

    public Tweet(String content, String time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOwner(String owner) {
        this.content += ". Author: " + owner;
    }

    @Override
    public String toString() {
        return time + "\n" + content;
    }
}
