package com.tissue.core.social;

import com.tissue.core.plan.Plan;

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

    private List<Impression> impressions;

    private Date createTime;
    private Date updateTime;
    private boolean verified = false;
    private boolean invitable = false;

    private List<User> friends;
    private List<User> declinedUsers;
    private List<Invitation> invitationsReceived;
    private List<Invitation> invitationsSent;

    private List<Plan> plans;

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

    public void setImpressions(List<Impression> impressions) {
        this.impressions = impressions;
    }

    public List<Impression> getImpressions() {
        return impressions;
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
        System.out.println("<<<id>>> " + id);
        System.out.println("<<<userId>>> " + userId);
        return id.equals(userId);
    }

    public void addFriend(User user) {
        if(friends == null) {
            friends = new ArrayList();
        }
        friends.add(user);
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void addDeclinedUser(User user) {
        if(declinedUsers == null) {
            declinedUsers = new ArrayList();
        }
        declinedUsers.add(user);
    }

    public void setDeclinedUsers(List<User> declinedUsers) {
        this.declinedUsers = declinedUsers;
    }

    public void addInvitationReceived(Invitation invitation) {
        invitationsReceived.add(invitation);
    }

    public void setInvitationsReceived(List<Invitation> invitationsReceived) {
        this.invitationsReceived = invitationsReceived;
    }

    public List<Invitation> getInvitationsReceived() {
        return invitationsReceived;
    }

    public void addInvitationSent(Invitation invitation) {
        invitationsSent.add(invitation);
    }

    public void setInvitationsSent(List<Invitation> invitatonsSent) {
        this.invitationsSent = invitationsSent;
    }

    public List<Invitation> getInvitationsSent() {
        return invitationsSent;
    }

    public boolean isFriend(String userId) {
        for(User user : friends) {
            if(userId.equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
    public boolean hasInvited(String userId) {
        List<String> ids = new ArrayList();
        for(Invitation inv : invitationsReceived) {
            if(userId.equals(inv.getInvitor().getId())) {
                return true;   
            } 
        }
        for(Invitation inv : invitationsSent) {
            if(userId.equals(inv.getInvitee().getId())) {
                return true;   
            } 
        }
        for(User user : declinedUsers) {
            if(userId.equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean canInvite(String userId) {
        if(isSelf(userId) || isFriend(userId) || hasInvited(userId)) {
            return false;
        }
        return true;
    }
    */

    public void setInvitable(boolean invitable) {
        this.invitable = invitable;
    }

    public boolean isInvitable() {
        return invitable;
    }

    public void addPlan(Plan plan) {
        if(plans == null) {
            plans = new ArrayList();
        }
        plans.add(plan);
    }

    public List<Plan> getPlans() {
        return plans;
    }
}
