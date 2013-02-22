package com.tissue.core.plan;

import com.tissue.core.social.Account;
import java.util.List;
import java.util.Date;

public class PostWrapper extends Post {

    private Post post;

    public PostWrapper(Post post) {
        this.post = post;    
    }

    @Override
    public void setId(String id) {
        post.setId(id);
    }

    @Override
    public String getId() {
        return post.getId();
    }

    @Override
    public Date getCreateTime() {
        return post.getCreateTime();
    }

    @Override
    public Account getAccount() {
        return post.getAccount();
    }

    @Override
    public String getTitle() {
        return post.getTitle();
    }

    @Override
    public String getContent() {
        return post.getContent();
    }

    @Override
    public String getType() {
        return post.getType();
    }

    @Override
    public Plan getPlan() {
        return post.getPlan();
    }

    @Override
    public boolean isOwner(String viewerId) {
        return post.isOwner(viewerId);
    }

    public boolean isDeleted() {
        return post.isDeleted();
    }

}
