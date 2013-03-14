package com.tissue.core.plan;

public class MessageReply extends Comment {

    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
