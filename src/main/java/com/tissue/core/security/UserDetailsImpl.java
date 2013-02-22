package com.tissue.core.security;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserDetailsImpl implements UserDetails {

    private String id;

    private String username;
    private String password;

    private String displayName;
    private String email;

    private Set<String> roles;

    private int inviteLimit;

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String name) {
        return (roles != null) && roles.contains(name);
    }

    public List<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(roles != null) {
            for(String role : roles) {
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                authorities.add(authority);
            }
        }

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

    public void setInviteLimit(int inviteLimit) {
        this.inviteLimit = inviteLimit;
    }

    public int getInviteLimit() {
        return inviteLimit;
    }

    public boolean canInvite() {
        return inviteLimit > 0;
    }
}
