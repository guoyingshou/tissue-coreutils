package com.tissue.core.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Topic {

    protected String id;
    protected String title;
    protected String content;
    protected Date createTime;
    protected Date updateTime;

    private boolean deleted = false;

    protected Account account;
    private Set<String> tags;
    private List<Plan> plans;
    private List<Post> posts;
 
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

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isOwner(String viewerAccountId) {
        if((viewerAccountId != null) && viewerAccountId.equals(account.getId())) {
            return true;
        }
        return false;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addPlan(Plan plan) {
        if(plans == null) {
            plans = new ArrayList();
        }
        plans.add(plan);
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public Plan getActivePlan() {
        if(plans != null) {
            for(Plan plan : plans) {
                if(plan.isActive())
                    return plan;
            }
        }
        return null;
    }

    public List<Plan> getArchivedPlans() {
        List<Plan> result = new ArrayList();

        if(plans != null) {
            for(Plan plan : plans) {
                if(!plan.isActive()) {
                    result.add(plan);
                }
            }
        }
        return result;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public long getPostsCount() {
        return posts.size();
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat(new DateTime(getCreateTime()), new DateTime());
    }

}
