package com.tissue.core.plan.dao;

import com.tissue.core.command.TopicCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import java.util.List;

public interface TopicDao {

    String create(TopicCommand command);

    void update(TopicCommand command);

    Topic getTopic(String id);

    List<Topic> getTrendingTopics(int num);

    List<Topic> getFeaturedTopics(int num);

    long getTopicsCount();

    List<Topic> getPagedTopics(int page, int size);

    long getTopicsCountByTag(String tag);

    List<String> getTopicTags();

    List<Topic> getPagedTopicsByTag(String tag, int page, int size);

    /**
     * posts
     */
    long getPostsCount(String topicId);

    List<Post> getPagedPosts(String topicId, int page, int size);

    long getPostsCountByType(String topicId, String type);

    List<Post> getPagedPostsByType(String topicId, String type, int page, int size);


}
