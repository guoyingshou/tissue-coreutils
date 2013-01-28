package com.tissue.core.social;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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

        args.add(what.getId());
        args.add(what.getDisplayName());

        args.add(to.getId());
        args.add(to.getDisplayName());

        args.add(where.getId());
        args.add(where.getDisplayName());

        return args;
    }
}
