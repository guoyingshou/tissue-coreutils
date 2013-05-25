package com.tissue.plan;

import com.tissue.core.TimeFormat;
import com.tissue.core.Account;
import com.tissue.core.Node;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Plan extends Node {

    private Integer duration;

    private Topic topic;
    private List<Account> members;

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void addMember(Account member) {
        if(members == null) {
            members = new ArrayList();
        }
        members.add(member);
    }

    public void setMembers(List<Account> members) {
        this.members = members;
    }

    public List<Account> getMembers() {
        if(members == null) {
            members = new ArrayList();
        }
        return members;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public Boolean isMember(String userAccountId) {
        if((members != null) && (userAccountId != null)) {
            for(Account account : members) {
                if(userAccountId.equals(account.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Date getEndTime() {
        DateTime dt = new DateTime(this.createTime).plusMonths(this.duration);
        return dt.toDate();
    }

    public Boolean isActive() {
        DateTime dt = new DateTime(this.createTime).plusMonths(this.duration);
        if(dt.isAfterNow()) {
            return true;
        }
        return false;
    }

    public TimeFormat getTimeRemaining() {
        return new TimeFormat(new DateTime(), new DateTime(createTime).plusMonths(duration));
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat(new DateTime(getCreateTime()), new DateTime());
    }
}
