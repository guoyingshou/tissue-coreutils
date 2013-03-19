package com.tissue.core.plan;

import java.util.List;
import java.util.ArrayList;

public class Article extends Post {

    private List<Message> messages;

    public void addMessage(Message message) {
        if(messages == null) {
            messages = new ArrayList();
        }
        messages.add(message);
    }

    /**
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    */

    public List<Message> getMessages() {
        return messages;
    }

}
