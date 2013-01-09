package com.tissue.core.plan;

import com.tissue.core.profile.User;
import java.util.List;
import java.util.Date;

public class Cnt extends PostWrapper {

    private List<PostMessage> messages;

    public Cnt(Post post) {
        super(post);
    }

    public void setMessages(List<PostMessage> messages) {
        this.messages = messages;
    }

    public List<PostMessage> getMessages() {
        return messages;
    }

}
