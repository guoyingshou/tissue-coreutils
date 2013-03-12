package com.tissue.core.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.Date;

public class AnswerComment {

    private String id;
    protected String title;
    protected String content;
    protected Date createTime;
    protected Date updateTime;

    protected Account account;
    private Answer answer;
   
    private boolean deleted = false;

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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isOwner(String viewerAccountId) {
        if((viewerAccountId != null) && viewerAccountId.equals(account.getId())) {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Answer getAnswer() {
        return answer;
    }
}
