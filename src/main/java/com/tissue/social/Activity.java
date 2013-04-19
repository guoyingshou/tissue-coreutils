package com.tissue.social;

import com.tissue.core.TimeFormat;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Activity implements Serializable {

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

        List<String> args = new ArrayList<String>();

        if(what != null && what.getId() != null) {
            args.add(what.getId().replace("#", ""));
            if(what.getDisplayName().length() > 24) {
               args.add(what.getDisplayName().substring(0, 24) + "..."); 
            }
            else {
                args.add(what.getDisplayName());
            }
        }

        if(where != null && where.getId() != null) {
            args.add(where.getId().replace("#", ""));
        }

        return args;
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat(new DateTime(createTime), new DateTime());

    }

}
