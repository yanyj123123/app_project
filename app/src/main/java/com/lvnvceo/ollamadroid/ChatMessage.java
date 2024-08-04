package com.lvnvceo.ollamadroid;

import com.google.gson.Gson;

import java.util.Date;

public class ChatMessage {
    private final int profileImage;
    private final String profileName;
    private final String messageContent;
    // 标记是发送还是接受
    private final boolean isSent;

    private boolean isFavorite;

    private final String date;

    private final long currentTimeMillis;


    public ChatMessage(int profileImage, String profileName, String messageContent , boolean isSent, boolean isFavorite, String date,long currentTimeMillis) {
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.messageContent = messageContent;
        this.isSent = isSent;
        this.isFavorite=isFavorite;
        this.date = date;
        this.currentTimeMillis=currentTimeMillis;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getMessageContent() {
        return messageContent;
    }
    public boolean isSent() {
        return isSent; // 新增getter方法
    }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite){
        this.isFavorite=favorite;
    }

    public String getDate(){
        return date;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ChatMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatMessage.class);
    }
}
