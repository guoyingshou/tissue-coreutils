package com.tissue.core.social;

import com.tissue.core.social.User;
import com.tissue.core.social.Account;
import java.io.Serializable;
import java.util.Date;

public class Impression implements Serializable {

    private String id;
    private String content;
    private Date createTime;

    private User user;
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
    
}
