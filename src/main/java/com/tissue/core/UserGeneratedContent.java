package com.tissue.core;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.security.AccessControlException;

public class UserGeneratedContent {

    protected String id;
    protected String content;
    protected String type;
    protected Date createTime;
    protected Date updateTime;
    protected boolean deleted = false;

    protected Account account;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content.trim();
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isOwner(Account viewerAccount) {
        if((viewerAccount != null) && viewerAccount.getId().equals(account.getId())) {
            return true;
        }
        return false;
    }

    /**
     * Intended to be used in controller.
     */
    public void checkPermission(Account viewerAccount, String role) {
        if((viewerAccount != null) && !viewerAccount.hasRole("ROLE_EVIL") && (viewerAccount.hasRole(role) || viewerAccount.getId().equals(account.getId()))) {
            return;
        }
        throw new AccessControlException("Access of " + id + " denied: " + account);
    }

    /** 
     * Intended to be used in view.
     */
    public boolean isAllowed(Account viewerAccount, String role) {
        if((viewerAccount == null) || viewerAccount.hasRole("ROLE_EVIL")) {
            return false;
        }
        if(viewerAccount.hasRole(role) || viewerAccount.getId().equals(account.getId())) {
            return true;
        }
        return false;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat(new DateTime(getCreateTime()), new DateTime());
    }

    public String toString() {
        return this.id;    
    }
}
