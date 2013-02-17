package com.tissue.core.social;

//import com.tissue.core.util.OrientIdentityUtil;
import java.io.Serializable;
import java.util.Date;

public class About implements Serializable {

    private String id;
    private String content;
    private Date published;

    private User user;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public Date getPublished() {
        return published;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    
}
