package com.tissue.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class User implements Serializable {

    private String id;
    private String displayName;
    private String headline;
    private int inviteLimit;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getHeadline() {
        return headline;
    }

    public void setInviteLimit(int inviteLimit) {
        this.inviteLimit = inviteLimit;
    }

    public int getInviteLimit() {
        return inviteLimit;
    }

}
