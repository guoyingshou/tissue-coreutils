package com.tissue.core.plan;

import com.tissue.core.social.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PostMessage extends ContentParent {

    private Post post;
    private List<PostMessageComment> comments;

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void addComment(PostMessageComment comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }

    public void setComments(List<PostMessageComment> comments) {
        this.comments = comments;
    }

    public List<PostMessageComment> getComments() {
        return comments;
    }

}
