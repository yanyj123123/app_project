package com.lvnvceo.ollamadroid.ollama;

// 历史聊天记录类
public class ChatItem {
    private String date;
    private String input;
    private String message;
    private boolean isDate;

    public ChatItem(String date,boolean isDate) {
        this.date = date;
        this.isDate = isDate;
    }

    public ChatItem(String input,String message) {
        this.message = message;
        this.input=input;
        this.isDate = false;
    }

    public String getDate() {
        return date;
    }
    public String getInput(){
        return input;
    }

    public String getMessage() {
        return message;
    }

    public boolean isDate() {
        return isDate;
    }
}

