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

    List<Connection> connections = new ArrayList();

    /**
    List<User> friends = new ArrayList();
    List<Invitation> invitations = new ArrayList();
    */

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

    public boolean isSelf(String userId) {
        return id.equals(userId);
    }

    public List<User> getFriends() {
        List<User> friends = new ArrayList();
        for(Connection conn : connections) {
            if("accepted".equals(conn.getStatus())) {
                User from = conn.getFrom();
                if(!from.isSelf(id)) {
                    friends.add(from);
                }
                else {
                   User to = conn.getTo();
                   friends.add(to);
                }
            }
        }
        return friends;
    }

    public boolean isFriend(String userId) {
        for(Connection conn : connections) {
            if("accepted".equals(conn.getStatus())) {
                User from = conn.getFrom();
                if(!from.isSelf(id)) {
                    if(userId.equals(from.getId())) {
                        return true;
                    }
                }
                else {
                   User to = conn.getTo();
                   if(userId.equals(to.getId())) {
                       return true;
                   }
                }
            }
        }
        return false;
    }

    public List<Invitation> getInvitationsReceived() {
        List<Invitation> result = new ArrayList();
        for(Connection conn : connections) {
            if(("invite".equals(conn.getStatus())) && (id.equals(conn.getTo().getId()))) {
                Invitation inv = new Invitation();
                inv.setId(conn.getId());
                inv.setInvitor(conn.getFrom());
                inv.setContent(conn.getContent());
                inv.setCreateTime(conn.getCreateTime());
                result.add(inv);
            }
        }
        return result;
    }

    public boolean canInvite(String userId) {
        if(isSelf(userId)) {
            return false;
        }
        for(Connection conn : connections) {
            User from = conn.getFrom();
            if(!from.isSelf(id)) {
                if(userId.equals(from.getId())) {
                    return false;
                }
            }
            else {
               User to = conn.getTo();
               if(userId.equals(to.getId())) {
                   return false;
               }
            }
        }
        return true;
    }

    public void addConnection(Connection conn) {
        connections.add(conn);
    }

    public static class Connection {
        private String id;

        private User from;
        private User to;

        private String status;
        private String content;
        private Date createTime;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
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
    }
}
