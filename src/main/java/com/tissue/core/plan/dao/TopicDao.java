package com.tissue.core.plan.dao;

import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import java.util.List;

public interface TopicDao {

    Topic create(Topic topic);

    void update(Topic topic);

    Topic getTopic(String id);

    Topic getTopicByPlanId(String planId);

    Topic getTopicByPostId(String postId);

    List<Topic> getTrendingTopics(int num);

    List<Topic> getFeaturedTopics(int num);

    long getTopicsCount();

    List<Topic> getPagedTopics(int page, int size);

    long getTopicsCountByTag(String tag);

    List<String> getTopicTags();

    List<Topic> getPagedTopicsByTag(String tag, int page, int size);

    /**
     * Get topics that the user has not learned.
     */
    List<Topic> getNewTopics(String excludingUserId, int limit);

}
