package com.tissue.core.social;

import java.util.Date;

public class Invitation {

    private String id;
    private String status;
    private String content;
    private Date createTime;
    private Date updateTime;

    private User invitor;
    private User invitee;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setInvitor(User invitor) {
        this.invitor = invitor; 
    }

    public User getInvitor() {
        return invitor;
    }

    public void setInvitee(User invitee) {
        this.invitee = invitee; 
    }

    public User getInvitee() {
        return invitee;
    }

}
