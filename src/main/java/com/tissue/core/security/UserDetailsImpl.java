package com.tissue.core.security;

import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

public class UserDetailsImpl implements UserDetails {

    private String id;

    private String displayName;
    private String email;

    private String username;
    private String password;

    List<User> friends = new ArrayList();
    List<Invitation> invitations = new ArrayList();

    private List<? extends GrantedAuthority> authorities;

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
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

    public String setUsername(String username) {
        return this.username = username;
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

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public List<User> getFriends() {
        return friends;
    }

    public boolean isFriend(String userId) {
        Set<String> ids = new HashSet();
        for(User user : friends) {
            ids.add(user.getId());
        }
        return ids.contains(userId);
    }

    public void addInvitation(Invitation invitation) {
        invitations.add(invitation);
    }

    public List<Invitation> getInvitationsReceived() {
        List<Invitation> result = new ArrayList();
        for(Invitation inv : invitations) {
            if(id.equals(inv.getInvitee().getId())) {
                result.add(inv);
            }
        }
        return result;
    }

    public List<Invitation> getInvitationsSent() {
        List<Invitation> result = new ArrayList();
        for(Invitation inv : invitations) {
            if(id.equals(inv.getInvitor().getId())) {
                result.add(inv);
            }
        }
        return result;
    }

}
