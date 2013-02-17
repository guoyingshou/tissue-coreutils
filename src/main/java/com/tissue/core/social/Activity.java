package com.tissue.core.social;

import com.tissue.core.util.TimeFormat;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Activity implements Serializable {

    //friends
    //topic, plan, members, concept, note, tutorial, question, 
    //postMessage, messageComment, questionComment, answer, answerComment
    protected String label;
    protected Date createTime;

    protected ActivityObject who;
    protected ActivityObject what;
    protected ActivityObject to;
    protected ActivityObject where;

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setWho(ActivityObject who) {
        this.who = who;
    }

    public ActivityObject getWho() {
        return who;
    }

    public void setWhat(ActivityObject what) {
        this.what = what;
    }

    public ActivityObject getWhat() {
        return what;
    }

    public void setTo(ActivityObject to) {
        this.to = to;
    }

    public ActivityObject getTo() {
        return to;
    }

    public void setWhere(ActivityObject where) {
        this.where = where;
    }

    public ActivityObject getWhere() {
        return where;
    }

    public List<String> getMessageArgs() {

        List<String> args = new ArrayList();

        if(what != null && what.getId() != null) {
            args.add(what.getId().replace("#", ""));
            args.add(what.getDisplayName());
        }

        if(to != null && to.getId() != null) {
            args.add(to.getId().replace("#", ""));
            args.add(to.getDisplayName());
        }

        if(where != null && where.getId() != null) {
            args.add(where.getId().replace("#", ""));
            args.add(where.getDisplayName());
        }

        return args;
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat() {
            public int getYears() {
                return getPeriod().getYears();
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

            public int getSeconds() {
                return getPeriod().getSeconds();
            }
        };
    }

    private Period getPeriod() {
        DateTime now = new DateTime();
        DateTime end = new DateTime(createTime);

        Period p = new Period(end, now);
        return p;
    }


}
