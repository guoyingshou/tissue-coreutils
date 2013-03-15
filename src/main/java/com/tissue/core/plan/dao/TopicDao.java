package com.tissue.core.plan.dao;

import com.tissue.core.command.TopicCommand;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface TopicDao {

    String create(TopicCommand command);

    void update(TopicCommand command);

    Topic getTopic(String topicId);

    Topic getTopicByPlan(String planId);

    List<Topic> getTrendingTopics(int num);

    List<Topic> getFeaturedTopics(int num);

    long getTopicsCount();

    List<Topic> getPagedTopics(int page, int size);

    long getTopicsCountByTag(String tag);

    List<String> getTopicTags();

    List<Topic> getPagedTopicsByTag(String tag, int page, int size);

    List<Topic> getNewTopics(String excludingUserId, int limit);

}
