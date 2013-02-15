package com.tissue.core.plan.dao;

import com.tissue.core.plan.Post;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.command.PostCommand;
import java.util.List;

public interface PostDao {

    /**
     * Add a post.
     */
    String create(PostCommand postCommand);

    Post update(Post post);

    /**
     * Get the specific post.
     */
    Post getPost(String id);


    //-- by topic
    long getPostsCountByTopicId(String topicId);

    List<Post> getPagedPostsByTopicId(String topicId, int page, int size);

    //-- by topic and type
    long getPostsCountByTopicIdAndType(String topicId, String type);

    List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size);

    //-- by plan
    long getPostsCountByPlanId(String planId);

    List<Post> getPagedPostsByPlanId(String planId, int page, int size);

    //-- by user

    long getPostsCountByUserId(String userId);

    List<Post> getPagedPostsByUserId(String userId, int page, int size);

    //by timestamp
    List<Post> getLatestPosts(int limit);

    Topic getTopic(String postId);
}
