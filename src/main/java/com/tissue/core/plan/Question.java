package com.tissue.core.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Question {

    protected String id;
    protected String title;
    protected String content;
    protected Date createTime;
    protected Date updateTime;
    private String type;
    private boolean deleted = false;

    protected Account account;
    private Plan plan;
    private List<QuestionComment> comments;
    private List<Answer> answers;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

    public void addComment(QuestionComment comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }

    public void setComments(List<QuestionComment> comments) {
        this.comments = comments;
    }

    public List<QuestionComment> getComments() {
        return comments;
    }

    public void addAnswer(Answer answer) {
        if(answers == null) {
            answers = new ArrayList();
        }
        answers.add(answer);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

}
