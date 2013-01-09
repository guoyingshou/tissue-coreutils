package com.tissue.core.plan.dao;

import com.tissue.core.plan.PostMessage;
import java.util.List;

public interface PostMessageDao {

    /**
     * Add a message to the specific post.
     */
    PostMessage create(PostMessage message);
    void update(PostMessage message);
    void delete(String messageId);

}
