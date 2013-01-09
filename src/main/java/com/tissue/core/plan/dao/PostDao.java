package com.tissue.core.plan.dao;

import com.tissue.core.plan.Post;
import java.util.List;

public interface PostDao {

    /**
     * Add a post.
     */
    Post create(Post post);

    Post update(Post post);

    /**
     * Get the specific post.
     */
    Post getPost(String id);

    //-- by topic
    long getPostsCountByTopicId(String topicId);

    List<Post> getPagedPostsByTopicId(String topicId, int page, int size);

    List<Post> getPostsByTopicId(String topicId);

    //-- by topic and type
    long getPostsCountByTopicIdAndType(String topicId, String type);

    List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size);

    List<Post> getPostsByTopicIdAndType(String topicId, String type);

    //-- by plan
    long getPostsCountByPlanId(String planId);

    List<Post> getPagedPostsByPlanId(String planId, int page, int size);

    List<Post> getPostsByPlanId(String planId);

    //-- by user

    long getPostsCountByUserId(String userId);

    List<Post> getPagedPostsByUserId(String userId, int page, int size);

    List<Post> getPostsByUserId(String userId);

}
