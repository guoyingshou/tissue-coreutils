package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.PostCommand;
import com.tissue.plan.Post;
import java.util.List;

public interface PostDao extends ContentDao {

    String create(PostCommand command);

    void update(PostCommand command);

    List<Post> getLatestPosts(int limit);
   
    long getPostsCountByUser(String userId);

    List<Post> getPagedPostsByUser(String userId, int page, int size);

    long getPostsCountByPlan(String planId);

    List<Post> getPagedPostsByPlan(String planId, int page, int size);

    long getPostsCountByTopic(String topicId);

    List<Post> getPagedPostsByTopic(String topicId, int page, int size);

    long getPostsCountByType(String topicId, String type);

    List<Post> getPagedPostsByType(String topicId, String type, int page, int size);

}
