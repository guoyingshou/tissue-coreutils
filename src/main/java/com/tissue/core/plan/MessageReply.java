package com.tissue.core.plan;

import com.tissue.core.UserGeneratedContent;

public class MessageReply extends UserGeneratedContent {

    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
