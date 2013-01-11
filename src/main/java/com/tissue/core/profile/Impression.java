package com.tissue.core.profile;

import java.io.Serializable;
import java.util.Date;

public class Impression implements Serializable {

    private String id;
    private String content;
    private Date published;

    private User from;
    private User to;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public Date getPublished() {
        return published;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getFrom() {
        return from;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public User getTo() {
        return to;
    }
    
}
