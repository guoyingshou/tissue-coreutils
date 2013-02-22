package com.tissue.core.plan.dao;

import com.tissue.core.command.PostCommand;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface PostDao {

    String create(PostCommand postCommand);

    void update(PostCommand post);

    Post getPost(String id);

    List<Post> getLatestPosts(int limit);

    /**
     * topic
     */
    Topic getTopic(String postId);

    String getTopicId(String postId);
    
}
