package com.tissue.core.plan;

import com.tissue.core.social.User;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Plan extends Parent {

    private Integer duration;

    private Topic topic;
    private List<User> members;

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void addMember(User member) {
        if(members == null) {
            members = new ArrayList();
        }
        members.add(member);
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<User> getMembers() {
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

    public Boolean isMember(String userId) {
        if((members != null) && (userId != null)) {
            for(User user : members) {
                if(userId.equals(user.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isActive() {
        DateTime dt = new DateTime(this.createTime).plusMonths(this.duration);
        if(dt.isAfterNow()) {
            return true;
        }
        return false;
    }

    private Period getPeriod() {
        DateTime now = new DateTime();
        DateTime end = new DateTime(createTime).plusMonths(duration);

        Period p = new Period(now, end);
        return p;
    }

    public int getMonths() {
        return getPeriod().getMonths();
    }

    public int getWeeks() {
        return getPeriod().getWeeks();
    }

    public int getDays() {
        return getPeriod().getDays();
    }

    public int getHours() {
        return getPeriod().getHours();
    }

    public int getMinutes() {
        return getPeriod().getMinutes();
    }

}
