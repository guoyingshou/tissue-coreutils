package com.tissue.core.social;

import java.io.Serializable;
import java.util.Date;

public class Impression implements Serializable {

    private String id;
    private String content;
    private Date createTime;

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

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
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
