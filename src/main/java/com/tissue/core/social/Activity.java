package com.tissue.core.social;

import java.io.Serializable;
import java.util.Date;

public class Activity implements Serializable {

    private String type;
    //private String title;
    private Date published;

    private ActivityObject who;
    private ActivityObject what;
    private ActivityObject to;
    private ActivityObject where;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    */

    public void setPublished(Date published) {
        this.published = published;
    }

    public Date getPublished() {
        return published;
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

}
