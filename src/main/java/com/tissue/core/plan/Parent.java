package com.tissue.core.plan;

import com.tissue.core.social.User;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Parent implements Serializable {

    protected String id;
    protected Date createTime;

    protected User user;
   
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public boolean isOwner(String viewerId) {
        if((viewerId != null) && viewerId.equals(user.getId())) {
            return true;
        }
        return false;
    }
}
