package com.tissue.core.plan.dao;

import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;

public interface PostMessageCommentDao {

    /**
     * Add a comment to the specific post message.
     */
    PostMessageComment create(PostMessageComment comment);

    PostMessageComment update(PostMessageComment comment);

    void delete(String commentId);
}
