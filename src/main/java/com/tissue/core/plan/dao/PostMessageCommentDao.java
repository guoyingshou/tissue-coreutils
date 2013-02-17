package com.tissue.core.plan.dao;

import com.tissue.core.command.PostMessageCommentCommand;
//import com.tissue.core.plan.PostMessage;
//import com.tissue.core.plan.PostMessageComment;

public interface PostMessageCommentDao {

    /**
     * Add a comment to the specific post message.
    String create(PostMessageComment comment);
    void update(PostMessageComment comment);
     */
    String create(PostMessageCommentCommand command);
    void update(PostMessageCommentCommand command);
 
    void delete(String commentId);
}
