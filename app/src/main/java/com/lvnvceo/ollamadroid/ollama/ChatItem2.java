package com.lvnvceo.ollamadroid.ollama;

public class ChatItem2 {
    private String question;
    private String answer;

    public ChatItem2(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
