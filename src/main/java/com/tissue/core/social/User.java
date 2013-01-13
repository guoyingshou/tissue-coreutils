package com.tissue.core.social;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class User implements Serializable {

    private String id;

    private String username;
    private String password;

    private String displayName;
    private String email;
    private String resume;

    private Date createTime;
    private Date updateTime;
    private Boolean verified = false;

    private List<User> friends;
    private Set<String> friendsIds;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getResume() {
        return resume;
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

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean isVerified() {
        return verified;
    }

    public Boolean isSame(String userId) {
        return id.equals(userId);   
    }

    public void addFriend(User friend) {
        if(friendsIds == null) {
            friendsIds = new HashSet();
        }
        friendsIds.add(friend.getId());

        if(friends == null) {
            friends = new ArrayList();
        }
        friends.add(friend);
    }

    public List<User> getFriends() {
        return friends;
    }

    public Boolean isFriend(String userId) {
        return (friendsIds != null) && friendsIds.contains(userId);
    }
}
