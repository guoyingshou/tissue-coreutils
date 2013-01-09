package com.tissue.core.plan;

public class PostMessageComment extends ContentParent {

    private PostMessage message;

    public void setPostMessage(PostMessage message) {
        this.message = message;
    }

    public PostMessage getPostMessage() {
        return message;
    }
}
