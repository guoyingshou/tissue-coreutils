package com.tissue.core.plan;

import com.tissue.core.social.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Cnt extends PostWrapper {

    private List<PostMessage> messages;

    public Cnt(Post post) {
        super(post);
    }

    public void addPostMessage(PostMessage message) {
        if(messages == null) {
            messages = new ArrayList();
        }
        messages.add(message);
    }

    public void setMessages(List<PostMessage> messages) {
        this.messages = messages;
    }

    public List<PostMessage> getMessages() {
        return messages;
    }

}
