package com.tissue.core.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;


public class Answer {

    protected String id;
    protected String title;
    protected String content;
    protected Date createTime;
    protected Date updateTime;

    private Question question;
    List<AnswerComment> comments;
    protected Account account;
   
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

    /**
    private Period getPeriod() {
        DateTime start = new DateTime(getCreateTime());
        DateTime now = new DateTime();

        Period p = new Period(start, now);
        return p;
    }
    */

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

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void addComment(AnswerComment comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }

    public void setComments(List<AnswerComment> comments) {
        this.comments = comments;
    }

    public List<AnswerComment> getComments() {
        return comments;
    }
}
