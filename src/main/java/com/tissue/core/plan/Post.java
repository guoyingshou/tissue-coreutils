package com.tissue.core.plan;

import java.util.List;

public class Post extends ContentParent {

    private Boolean closed = false;

    private String type;

    private Plan plan;

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean isClosed() {
        return closed;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

}
