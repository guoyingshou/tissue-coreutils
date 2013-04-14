package com.tissue.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Account implements Serializable {

    private String id;
    private String username;
    private String password;
    private String email;
    Set<String> roles = new HashSet<String>();

    private User user;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || (obj.getClass() != this.getClass())) {
            return false;
        }
        Account that = (Account)obj;
        return username.equals(that.username);
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + username.hashCode();
        return hash;
    }

    public String toString() {
        return this.id;
    }
}
