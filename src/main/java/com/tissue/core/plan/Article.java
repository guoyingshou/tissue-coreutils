package com.tissue.core.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Article extends Post {

    private Plan plan;
    private List<PostMessage> messages;

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
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
