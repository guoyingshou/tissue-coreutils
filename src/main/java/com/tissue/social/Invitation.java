package com.tissue.social;

import com.tissue.core.User;
import com.tissue.core.Account;

import java.io.Serializable;
import java.util.Date;

public class Invitation implements Serializable {

    private String id;
    private String content;
    private Date createTime;

    private User to;
    private Account account;

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

    public void setTo(User to) {
        this.to = to;
    }

    public User getTo() {
        return to;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
