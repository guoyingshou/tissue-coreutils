package com.tissue.core.plan.dao;

import com.tissue.core.plan.PostMessage;
import com.tissue.core.command.PostMessageCommand;
import java.util.List;

public interface PostMessageDao {

    /**
     * Add a message to the specific post.
     */
    PostMessage create(PostMessageCommand command);

    void update(PostMessageCommand command);

    void delete(String messageId);

}
